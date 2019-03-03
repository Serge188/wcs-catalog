package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import ru.wcscatalog.core.dto.*;
import ru.wcscatalog.core.model.*;
import ru.wcscatalog.core.utils.AliasChecker;
import ru.wcscatalog.core.utils.Transliterator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {
    private final EntityManagerFactory entityManagerFactory;
    private final CategoriesRepository categoriesRepository;
    private final FactoryRepository factoryRepository;
    private final ImageRepository imageRepository;
    private final OptionsRepository optionsRepository;
    private final AliasChecker aliasChecker;
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;

    public ProductRepository(EntityManagerFactory entityManagerFactory, CategoriesRepository categoriesRepository,
                             FactoryRepository factoryRepository, ImageRepository imageRepository,
                             OptionsRepository optionsRepository, AliasChecker aliasChecker) {
        this.entityManagerFactory = entityManagerFactory;
        this.categoriesRepository = categoriesRepository;
        this.factoryRepository = factoryRepository;
        this.imageRepository = imageRepository;
        this.optionsRepository = optionsRepository;
        this.aliasChecker = aliasChecker;
        initializeCriteriaBuilder();
    }

    public List<ProductEntry> getPopularProducts() {
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> root = criteriaQuery.from(Product.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("popular"), true));
        Query query = entityManager.createQuery(criteriaQuery);
        List<Product> products = query.getResultList();
        List<ProductEntry> entries = new ArrayList<>();
        for (Product p: products) {
            entries.add(ProductEntry.fromProduct(p));
        }
        fillSaleOffers(entries);
        return entries;
    }

    public Product getProductById(Long id) {
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> root = criteriaQuery.from(Product.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));
        Optional<Product> product = entityManager.createQuery(criteriaQuery).getResultList().stream().findAny();
        return product.orElse(null);
    }

    public SaleOffer getSaleOfferById(Long id) {
        CriteriaQuery<SaleOffer> criteriaQuery = criteriaBuilder.createQuery(SaleOffer.class);
        Root<SaleOffer> root = criteriaQuery.from(SaleOffer.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));
        Optional<SaleOffer> saleOffer = entityManager.createQuery(criteriaQuery).getResultList().stream().findAny();
        return saleOffer.orElse(null);
    }

    public ProductEntry getProductByAlias(String alias) {
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> root = criteriaQuery.from(Product.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("alias"), alias));
        Query query = entityManager.createQuery(criteriaQuery);
        Product product = (Product) query.getSingleResult();
        ProductEntry entry = ProductEntry.fromProduct(product);
        fillSaleOffers(Collections.singletonList(entry));
        fillOptions(Collections.singletonList(entry));
        return entry;
    }

    public List<ProductEntry> getProductsByCategory(Long categoryId) {
        CriteriaQuery<Category> categoryCriteriaQuery = criteriaBuilder.createQuery(Category.class);
        Root<Category> categoryRoot = categoryCriteriaQuery.from(Category.class);
        categoryCriteriaQuery.select(categoryRoot.get("id"));
        categoryCriteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.isNotNull(categoryRoot.get("parentCategory")),
                criteriaBuilder.equal(categoryRoot.get("parentCategory"), categoryId)));

        Query categoryQuery = entityManager.createQuery(categoryCriteriaQuery);
        List<Long> categoryIds = categoryQuery.getResultList();
        categoryIds.add(categoryId);

        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);

        Root<Product> root = criteriaQuery.from(Product.class);
        Join categoryJoin = root.join("category");
        criteriaQuery.where(categoryJoin.get("id").in(categoryIds));
        Query query = entityManager.createQuery(criteriaQuery);
        List<Product> products = query.getResultList();
        List<ProductEntry> entries = products.stream().map(ProductEntry::fromProduct).collect(Collectors.toList());
        fillSaleOffers(entries);
        fillOptions(entries);
        return entries;
    }

    public List<ProductEntry> getProductsByCategoryForOneLevel(long categoryId) {
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> root = criteriaQuery.from(Product.class);
        Join categoryJoin = root.join("category");
        criteriaQuery.where(criteriaBuilder.equal(categoryJoin.get("id"), categoryId));
        Query query = entityManager.createQuery(criteriaQuery);
        List<Product> products = query.getResultList();
        List<ProductEntry> entries = products.stream().map(ProductEntry::fromProduct).collect(Collectors.toList());
        fillSaleOffers(entries);
        fillOptions(entries);
        return entries;
    }

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

        product.setDescription(input.getDescription());
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
        Factory factory = factoryRepository.getFactoryById(input.getFactoryId());
        if (factory != null) {
            product.setFactory(factory);
        }
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
            entityManager.merge(image);
        }

        if (product.getImages() != null && input.getImagesInput() != null && !input.getImagesInput().isEmpty()) {
            List<Image> images = product.getImages()
                    .stream()
                    .filter(x -> x.isMainImage() != null && x.isMainImage())
                    .collect(Collectors.toList());
            for (Image i : images) {
                i.setProduct(product);
                entityManager.merge(i);
            }
        }

        for (Object o: input.getImagesInput()) {

            String data = ((String) o);
            Image image = imageRepository.createImageForObject(product, data);
            image.setMainImage(false);
            image.setProduct(product);
            entityManager.merge(image);
        }

        entityManager.getTransaction().begin();
        entityManager.persist(product);
        entityManager.getTransaction().commit();

        for (OfferOptionInput optionInput: input.getOptions()) {
            ProductOptionsRelation relation = new ProductOptionsRelation();
            relation.setProduct(product);
            relation.setOption(optionsRepository.getOptionById(optionInput.getId()));
            relation.setValue(optionsRepository.getOptionValueById(optionInput.getSelectedValue().getId()));
            entityManager.getTransaction().begin();
            entityManager.persist(relation);
            entityManager.getTransaction().commit();
        }

        for (SaleOfferInput soi: input.getSaleOffers()) {
            createOrUpdateSaleOffer(soi, product);
        }
    }

    public SaleOffer createOrUpdateSaleOffer(SaleOfferInput input, Product product) throws Exception{
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
        OfferOption option = optionsRepository.getOptionById(input.getOfferOption().getId());
        saleOffer.setOfferOption(option);
        OptionValue value = optionsRepository.getOptionValueById(input.getOptionValue().getId());
        saleOffer.setOptionValue(value);
        if (input.getImageInput() != null) {
            String data = ((String) input.getImageInput());
            Image image = imageRepository.createImageForObject(saleOffer, data);
            saleOffer.setMainImage(image);
        }
        entityManager.getTransaction().begin();
        entityManager.persist(saleOffer);
        entityManager.getTransaction().commit();
        return saleOffer;
    }

    public void removeProduct(Long productId) {
        Product product = getProductById(productId);
        if (product != null) {
            CriteriaQuery<SaleOffer> criteriaQuery = criteriaBuilder.createQuery(SaleOffer.class);
            Root<SaleOffer> root = criteriaQuery.from(SaleOffer.class);
            Join productJoin = root.join("product");
            criteriaQuery.where(criteriaBuilder.equal(productJoin.get("id"), productId));
            Query query = entityManager.createQuery(criteriaQuery);
            List<SaleOffer> saleOffers = query.getResultList();

            saleOffers.forEach(so -> entityManager.remove(so));

            CriteriaQuery<ProductOptionsRelation> relationsCriteriaQuery = criteriaBuilder.createQuery(ProductOptionsRelation.class);
            Root<ProductOptionsRelation> relationsRoot = relationsCriteriaQuery.from(ProductOptionsRelation.class);
            Join relatedProductJoin = relationsRoot.join("product");
            criteriaQuery.where(criteriaBuilder.equal(relatedProductJoin.get("id"), productId));
            Query relationsQuery = entityManager.createQuery(relationsCriteriaQuery);
            List<ProductOptionsRelation> optionRelations = relationsQuery.getResultList();

            optionRelations.forEach(r -> entityManager.remove(r));
            imageRepository.removeImageForObject(product);

            entityManager.getTransaction().begin();
            entityManager.remove(product);
            entityManager.getTransaction().commit();
        }
    }

    public void removeSaleOfferFromProduct(Long saleOfferId) {
        SaleOffer saleOffer = getSaleOfferById(saleOfferId);
        if (saleOffer != null) {
            entityManager.getTransaction().begin();
            entityManager.remove(entityManager.contains(saleOffer) ? saleOffer : entityManager.merge(saleOffer));
            entityManager.getTransaction().commit();
        }
        imageRepository.removeImageForObject(saleOffer);
    }

    private void initializeCriteriaBuilder() {
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
        }
        criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    private void fillSaleOffers(List<ProductEntry> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        List<Long> productIds = products.stream().map(ProductEntry::getId).collect(Collectors.toList());
        CriteriaQuery<SaleOffer> criteriaQuery = criteriaBuilder.createQuery(SaleOffer.class);
        Root<SaleOffer> offer = criteriaQuery.from(SaleOffer.class);
        Join product = offer.join("product");
        criteriaQuery.where(product.get("id").in(productIds));
        Query query = entityManager.createQuery(criteriaQuery);
        List<SaleOffer> offers = query.getResultList();
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
        CriteriaQuery<ProductOptionsRelation> criteriaQuery = criteriaBuilder.createQuery(ProductOptionsRelation.class);
        Root<ProductOptionsRelation> offer = criteriaQuery.from(ProductOptionsRelation.class);
        Join product = offer.join("product");
        criteriaQuery.where(product.get("id").in(productIds));
        Query query = entityManager.createQuery(criteriaQuery);
        List<ProductOptionsRelation> optionRelations = query.getResultList();
        products.forEach(p -> {
            Optional<ProductOptionsRelation> r = optionRelations
                    .stream()
                    .filter(x -> x.getProduct().getId().equals(p.getId()))
                    .findAny();
            if (r.isPresent()) {
                OfferOptionEntry option = OfferOptionEntry.fromOfferOption(r.get().getOption());
                OptionValueEntry value = OptionValueEntry.fromOptionValue(r.get().getValue());
                if (option != null && value!=  null) {
                    option.setSelectedValue(value);
                    p.getOptions().add(option);
                }
            }
        });
    }
}
