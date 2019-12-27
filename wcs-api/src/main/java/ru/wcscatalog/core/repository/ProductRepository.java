package ru.wcscatalog.core.repository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.wcscatalog.core.dto.*;
import ru.wcscatalog.core.model.*;
import ru.wcscatalog.core.utils.AliasChecker;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class ProductRepository {
    private final CategoriesRepository categoriesRepository;
    private final FactoryRepository factoryRepository;
    private final ImageRepository imageRepository;
    private final OptionsRepository optionsRepository;
    private final AliasChecker aliasChecker;
    private Dao dao;

    public ProductRepository(CategoriesRepository categoriesRepository,
                             FactoryRepository factoryRepository, ImageRepository imageRepository,
                             OptionsRepository optionsRepository, AliasChecker aliasChecker, Dao dao) {
        this.categoriesRepository = categoriesRepository;
        this.factoryRepository = factoryRepository;
        this.imageRepository = imageRepository;
        this.optionsRepository = optionsRepository;
        this.aliasChecker = aliasChecker;
        this.dao = dao;
    }

    public List<ProductEntry> getPopularProducts() {
        List<ProductEntry> products = dao.listByProperty("popular", true, Product.class)
                .stream()
                .map(ProductEntry::fromProduct)
                .collect(Collectors.toList());
        fillSaleOffers(products);
        return products;
    }

    public Product getProductById(Long id) {
        return dao.byId(id, Product.class);
    }

    public SaleOffer getSaleOfferById(Long id) {
        return dao.byId(id, SaleOffer.class);
    }

    public ProductEntry getProductByAlias(String alias) {
        Product product = dao.singleByProperty("alias", alias, Product.class);
        ProductEntry entry = ProductEntry.fromProduct(product);
        fillSaleOffers(Collections.singletonList(entry));
        fillOptions(Collections.singletonList(entry));
        return entry;
    }

    public List<ProductEntry> getProductByFactory(String factoryAlias) {
        CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> root = criteriaQuery.from(Product.class);
        Join factoryJoin = root.join("factory");
        criteriaQuery.where(criteriaBuilder.equal(factoryJoin.get("alias"), factoryAlias));
        List<Product> products = dao.createQuery(criteriaQuery);

        List<ProductEntry> entries = products.stream().map(ProductEntry::fromProduct).collect(Collectors.toList());
        fillSaleOffers(entries);
        fillOptions(entries);
        return entries;
    }

    public Product getProductByTitleAndCategory(String title, Long categoryId) {
        CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> root = criteriaQuery.from(Product.class);
        criteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.equal(root.get("title"), title),
                criteriaBuilder.equal(root.get("category"), categoryId)));
        return dao.createQuery(criteriaQuery).stream().findFirst().orElse(null);
    }

    public List<ProductEntry> getProductsByCategory(Long categoryId, CategoryFilter filter) {
        CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
        CriteriaQuery<Category> categoryCriteriaQuery = criteriaBuilder.createQuery(Category.class);
        Root<Category> categoryRoot = categoryCriteriaQuery.from(Category.class);
        categoryCriteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.isNotNull(categoryRoot.get("parentCategory")),
                criteriaBuilder.equal(categoryRoot.get("parentCategory"), categoryId)));
        List<Long> categoryIds = dao.createQuery(categoryCriteriaQuery)
                .stream()
                .map(Category::getId).collect(Collectors.toList());
        categoryIds.add(categoryId);

        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);

        Root<Product> root = criteriaQuery.from(Product.class);
        Join categoryJoin = root.join("category");
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(categoryJoin.get("id").in(categoryIds));
        if (filter != null) {
            if (filter.getMaxPrice() != null && filter.getMaxPrice() != 0) {
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.and(
                                        criteriaBuilder.isNotNull(root.get("discountPrice")),
                                        criteriaBuilder.le(root.get("discountPrice"), filter.getMaxPrice())),
                                criteriaBuilder.and(
                                        criteriaBuilder.le(root.get("price"), filter.getMaxPrice()),
                                        criteriaBuilder.isNull(root.get("discountPrice"))),
                                criteriaBuilder.isNull(root.get("price"))
                        ));
            }
            if (filter.getMinPrice() != null && filter.getMinPrice() != 0) {
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.and(
                                        criteriaBuilder.isNotNull(root.get("discountPrice")),
                                        criteriaBuilder.ge(root.get("discountPrice"), filter.getMinPrice())),
                                criteriaBuilder.and(
                                        criteriaBuilder.ge(root.get("price"), filter.getMinPrice()),
                                        criteriaBuilder.isNull(root.get("discountPrice"))),
                                criteriaBuilder.isNull(root.get("price"))
                ));
            }
            if (filter.getFactoryIds() != null && !filter.getFactoryIds().isEmpty()) {
                for (Long fid: filter.getFactoryIds()) {
                    Join factoryJoin = root.join("factory");
                    predicates.add(criteriaBuilder.equal(factoryJoin.get("id"), fid));
                }
            }
            if (filter.getOptions() != null && !filter.getOptions().isEmpty()) {
                List<Long> valueIds = new ArrayList<>();
                for (OfferOptionInput ooi: filter.getOptions()) {
                    valueIds.addAll(ooi.getValues().stream().map(OptionValueInput::getId).collect(Collectors.toList()));
                }
                Subquery productOptionRelationSubquery = criteriaQuery.subquery(ProductOptionsRelation.class);
                Root subqueryRoot = productOptionRelationSubquery.from(ProductOptionsRelation.class);
                Join valueSubqueryJoin = subqueryRoot.join("value");
                Join productSubqueryJoin = subqueryRoot.join("product");
                productOptionRelationSubquery.where(valueSubqueryJoin.get("id").in(valueIds));
                productOptionRelationSubquery.select(productSubqueryJoin.get("id"));
                predicates.add(root.get("id").in(productOptionRelationSubquery));
            }
        }

        Predicate[] pr = new Predicate[predicates.size()];
        predicates.toArray(pr);
        criteriaQuery.where(pr);
        List<Product> products = dao.createQuery(criteriaQuery);
        List<ProductEntry> entries = products.stream().map(ProductEntry::fromProduct).collect(Collectors.toList());
        fillSaleOffers(entries);
        fillOptions(entries);

        if (filter != null) {
            entries = entries
                    .stream()
                    .filter(e -> {
                        if (filter.getMaxPrice() != null) {
                            return e.getPrice() != null
                                    || e.getSaleOffers().stream().anyMatch(so -> so.getPrice() <= filter.getMaxPrice());
                        }
                        return true;
                    })
                    .filter(e -> {
                        if (filter.getMinPrice() != null) {
                            return e.getPrice() != null
                                    || e.getSaleOffers().stream().anyMatch(so -> so.getPrice() >= filter.getMinPrice());
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
        }
        return entries;
    }

    public List<ProductEntry> getProductsByCategoryForOneLevel(long categoryId) {
        CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> root = criteriaQuery.from(Product.class);
        Join categoryJoin = root.join("category");
        criteriaQuery.where(criteriaBuilder.equal(categoryJoin.get("id"), categoryId));
        List<Product> products = dao.createQuery(criteriaQuery);

        List<ProductEntry> entries = products.stream().map(ProductEntry::fromProduct).collect(Collectors.toList());
        fillSaleOffers(entries);
        fillOptions(entries);
        return entries;
    }

    @PreAuthorize("isAuthenticated()")
    public void createOrUpdateProduct(ProductInput input) throws Exception{
        Product product = null;
        if (input.getId() != null) {
            product = getProductById(input.getId());
        }
        if (product == null) {
            product = new Product();
            product.setAlias(aliasChecker.findUniqueAliasForEntity(product.getClass(), input.getTitle()));
        }
        product.setTitle(input.getTitle());

        if (input.getDescription() != null) {
            product.setDescription(input
                    .getDescription()
                    .replaceAll("\\n", "<br/>")
                    .replaceAll("\\t", "&nbsp;&nbsp;&nbsp;"));
        }

        product.setProductOfDay(input.getProductOfDay());
        product.setNewProduct(input.getNewProduct());
        product.setPromo(input.getPromo());
        product.setPopular(input.getPopular());
        product.setHit(input.getHit());
        product.setPrice(input.getPrice());
        product.setDiscountPrice(input.getDiscountPrice());
        product.setCategory(categoriesRepository.getCategoryById(input.getCategoryId()));
        product.setFactory(factoryRepository.getFactoryById(input.getFactoryId()));
        product.setPriceType(input.getPriceType());

        if (product.getId() == null) {
            dao.add(product);
        }

//        Factory factory = factoryRepository.getFactoryById(input.getFactoryId());
//        if (factory != null) {
//            product.setFactory(factory);
//        }
        if (input.getImageInput() != null) {
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                Optional<Image> mainImage = product.getImages()
                        .stream()
                        .filter(x -> x.isMainImage() != null)
                        .filter(Image::isMainImage).findAny();
                if (mainImage.isPresent()) {
                    imageRepository.removeProductImage(mainImage.get());
                }
            }
            String data = ((String) input.getImageInput());
            Image image = imageRepository.createImageForObject(product, data);
            image.setMainImage(true);
            image.setProduct(product);
            dao.add(image);
        }

        if (input.getImagesInput() != null && !input.getImagesInput().isEmpty()) {
            for (Object o: input.getImagesInput()) {
                String data = ((String) o);
                Image image = imageRepository.createImageForObject(product, data);
                image.setMainImage(false);
                image.setProduct(product);
                dao.add(image);
            }
        }

        if (product.getId() != null) {
            CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
            CriteriaQuery<ProductOptionsRelation> criteriaQuery = criteriaBuilder.createQuery(ProductOptionsRelation.class);
            Root<ProductOptionsRelation> root = criteriaQuery.from(ProductOptionsRelation.class);
            criteriaQuery.where(criteriaBuilder.equal(root.get("product"), product.getId()));
            dao.createQuery(criteriaQuery).forEach(dao::remove);
        }


        for (OfferOptionInput optionInput: input.getOptions()) {
            ProductOptionsRelation relation = new ProductOptionsRelation();
            relation.setProduct(product);
            relation.setOption(optionsRepository.getOptionById(optionInput.getId()));
            relation.setValue(optionsRepository.getOptionValueById(optionInput.getSelectedValue().getId()));
            dao.add(relation);
        }

        for (SaleOfferInput soi: input.getSaleOffers()) {
            createOrUpdateSaleOffer(soi, product);
        }
    }

    @PreAuthorize("isAuthenticated()")
    public SaleOffer createOrUpdateSaleOffer(SaleOfferInput input, Product product) throws Exception {
        SaleOffer saleOffer = null;
        if (input.getId() != null) {
            saleOffer = getSaleOfferById(input.getId());
        }
        if (saleOffer == null) {
            saleOffer = new SaleOffer();
        }
        if (product == null) {
            product = getProductById(input.getProductId());
        }
        saleOffer.setProduct(product);
        saleOffer.setPrice(input.getPrice());
        saleOffer.setDiscountPrice(input.getDiscountPrice());
        dao.add(saleOffer);
        OfferOption option = optionsRepository.getOptionById(input.getOfferOption().getId());
        saleOffer.setOfferOption(option);
        OptionValue value = optionsRepository.getOptionValueById(input.getOptionValue().getId());
        saleOffer.setOptionValue(value);
        if (input.getImageInput() != null) {
            String data = ((String) input.getImageInput());
            Image image = imageRepository.createImageForObject(saleOffer, data);
            saleOffer.setMainImage(image);
        }
        return saleOffer;
    }

    @PreAuthorize("isAuthenticated()")
    public void removeProduct(Long productId) {
        Product product = getProductById(productId);
        if (product != null) {
            CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
            CriteriaQuery<SaleOffer> criteriaQuery = criteriaBuilder.createQuery(SaleOffer.class);
            Root<SaleOffer> root = criteriaQuery.from(SaleOffer.class);
            Join productJoin = root.join("product");
            criteriaQuery.where(criteriaBuilder.equal(productJoin.get("id"), productId));
            List<SaleOffer> saleOffers = dao.createQuery(criteriaQuery);

            saleOffers.forEach(dao::remove);

            CriteriaQuery<ProductOptionsRelation> relationsCriteriaQuery = criteriaBuilder.createQuery(ProductOptionsRelation.class);
            Root<ProductOptionsRelation> relationsRoot = relationsCriteriaQuery.from(ProductOptionsRelation.class);
            Join relatedProductJoin = relationsRoot.join("product");
            criteriaQuery.where(criteriaBuilder.equal(relatedProductJoin.get("id"), productId));
            List<ProductOptionsRelation> optionRelations = dao.createQuery(relationsCriteriaQuery);



            optionRelations.forEach(dao::remove);
            imageRepository.removeImageForObject(product);
            dao.remove(product);
        }
    }

    @PreAuthorize("isAuthenticated()")
    public void removeSaleOfferFromProduct(Long saleOfferId) {
        SaleOffer saleOffer = getSaleOfferById(saleOfferId);
        if (saleOffer != null) {
            dao.remove(saleOffer);
        }
        imageRepository.removeImageForObject(saleOffer);
    }

    private void fillSaleOffers(List<ProductEntry> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        List<Long> productIds = products.stream().map(ProductEntry::getId).collect(Collectors.toList());
        CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
        CriteriaQuery<SaleOffer> criteriaQuery = criteriaBuilder.createQuery(SaleOffer.class);
        Root<SaleOffer> offer = criteriaQuery.from(SaleOffer.class);
        Join product = offer.join("product");
        criteriaQuery.where(product.get("id").in(productIds));
        List<SaleOffer> offers = dao.createQuery(criteriaQuery);
        products.forEach(p -> {
            List<SaleOfferEntry> so = offers
                    .stream()
                    .filter(x -> x.getProduct().getId().equals(p.getId()))
                    .map(SaleOfferEntry::fromSaleOffer)
                    .collect(Collectors.toList());
            p.setSaleOffers(so);
        });
    }

    private void fillOptions(List<ProductEntry> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        List<Long> productIds = products.stream().map(ProductEntry::getId).collect(Collectors.toList());
        CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
        CriteriaQuery<ProductOptionsRelation> criteriaQuery = criteriaBuilder.createQuery(ProductOptionsRelation.class);
        Root<ProductOptionsRelation> offer = criteriaQuery.from(ProductOptionsRelation.class);
        Join product = offer.join("product");
        criteriaQuery.where(product.get("id").in(productIds));
        List<ProductOptionsRelation> optionRelations = dao.createQuery(criteriaQuery);
        products.forEach(p -> {
            List<ProductOptionsRelation> relationList = optionRelations
                    .stream()
                    .filter(x -> x.getProduct().getId().equals(p.getId()))
                    .collect(Collectors.toList());
            relationList.forEach(r -> {
                OfferOptionEntry option = OfferOptionEntry.fromOfferOption(r.getOption());
                OptionValueEntry value = OptionValueEntry.fromOptionValue(r.getValue());
                if (option != null && value!=  null) {
                    option.setSelectedValue(value);
                    p.getOptions().add(option);
                }
            });
        });
    }

    private List<ProductEntry> getProductsByIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) return new ArrayList<>();
        CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> product = criteriaQuery.from(Product.class);
        criteriaQuery.where(product.get("id").in(productIds));
        List<ProductEntry> entries = new ArrayList<>();
        dao.createQuery(criteriaQuery).forEach(p -> {
            entries.add(ProductEntry.fromProduct(p));
        });
        return entries;
    }

    public List<ProductSimplifiedEntry> getSimplifiedProducts(List<Long> productIds) {
        List<ProductSimplifiedEntry> entries = new LinkedList<>();
        List<ProductEntry> products = getProductsByIds(productIds);
        products.forEach(p -> {
            ProductSimplifiedEntry entry = new ProductSimplifiedEntry();
            entry.setId(p.getId());
            ImageEntry img = p.getMainImage();
            if (img != null) entry.setImageLink(img.getPreviewImageLink());
            entry.setPrice(BigDecimal.valueOf(p.getPrice()));
            entry.setTitle(p.getTitle());
            entry.setAlias(p.getAlias());
            entries.add(entry);
        });
        return entries;
    }

    public List<ProductSimplifiedEntry> getSimplifiedProductsWithOffers(List<IdQtyEntry> inputs) {
        List<ProductSimplifiedEntry> entries = new LinkedList<>();
        List<ProductEntry> products = getProductsByIds(inputs.stream().map(IdToIdEntry::getPrimaryId).collect(Collectors.toList()));
        fillSaleOffers(products);
        inputs.forEach(i -> {
            Optional<ProductEntry> product = products.stream().filter(p -> p.getId().equals(i.getPrimaryId())).findAny();
            product.ifPresent(p -> {
                ProductSimplifiedEntry entry = new ProductSimplifiedEntry();
                entry.setId(p.getId());
                ImageEntry img = p.getMainImage();

                Float price = p.getPrice();
                String title = p.getTitle();
                if (i.getSecondaryId() != null) {
                    Optional<SaleOfferEntry> currentSaleOffer = p
                            .getSaleOffers()
                            .stream()
                            .filter(so -> so.getId().equals(i.getSecondaryId())).findAny();
                    if (currentSaleOffer.isPresent()) {
                        if (currentSaleOffer.get().getMainImage() != null) {
                            img = currentSaleOffer.get().getMainImage();
                        }
                        price = currentSaleOffer.get().getPrice();
                        title = p.getTitle() + " (" + currentSaleOffer.get().getOptionValue().getValue() + ")";
                        entry.setCurrentSaleOfferId(currentSaleOffer.get().getId());
                    }

                }
                if (img != null) entry.setImageLink(img.getPreviewImageLink());
                if (price != null) {
                    entry.setPrice(BigDecimal.valueOf(price));
                }
                entry.setTitle(title);
                entry.setAlias(p.getAlias());
                if (i.getQty() == null || i.getQty() < 1) {
                    entry.setQty(1);
                } else {
                    entry.setQty(i.getQty());
                }
                if (entry.getQty() != null  && entry.getPrice() != null) {
                    entry.setSum(entry.getPrice().multiply(BigDecimal.valueOf(entry.getQty())));
                }
                entries.add(entry);
            });
        });
        return entries;
    }
}
