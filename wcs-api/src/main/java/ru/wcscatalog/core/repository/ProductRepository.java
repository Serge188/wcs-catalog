package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import ru.wcscatalog.core.dto.ProductEntry;
import ru.wcscatalog.core.dto.ProductInput;
import ru.wcscatalog.core.dto.SaleOfferEntry;
import ru.wcscatalog.core.dto.SaleOfferInput;
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
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;

    public ProductRepository(EntityManagerFactory entityManagerFactory, CategoriesRepository categoriesRepository,
                             FactoryRepository factoryRepository, ImageRepository imageRepository,
                             OptionsRepository optionsRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.categoriesRepository = categoriesRepository;
        this.factoryRepository = factoryRepository;
        this.imageRepository = imageRepository;
        this.optionsRepository = optionsRepository;
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
        return entries;
    }

    public void createOrUpdateProduct(ProductInput input) throws Exception{
        Product product = null;
        if (input.getId() != null) {
            product = getProductById(input.getId());
        }
        if (product == null) {
            product = new Product();
            product.setAlias(AliasChecker.findUniqueAliasForEntity(product.getClass(), input.getTitle()));
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
        product.setFactory(factoryRepository.getFactoyById(input.getFactoryId()));
        if (input.getMainImageInput() != null) {
            String data = ((String) input.getMainImageInput());
            Image image = imageRepository.createImageForObject(product, data);
            product.setMainImage(image);
        }
        for (Object o: input.getImageInputs()) {
            String data = ((String) o);
            Image image = imageRepository.createImageForObject(product, data);
            product.getImages().add(image);
        }
        for (SaleOfferInput soi: input.getSaleOffers()) {
            createOrUpdateSaleOffer(soi, product);
        }
        entityManager.getTransaction().begin();
        entityManager.persist(product);
        entityManager.getTransaction().commit();
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
        if (input.getMainImage() != null) {
            String data = ((String) input.getMainImage());
            Image image = imageRepository.createImageForObject(saleOffer, data);
            saleOffer.setMainImage(image);
        }
        saleOffer.setOfferOption(optionsRepository.createOrUpdateOption(input.getOfferOption()));
        saleOffer.setOptionValue(input.getOptionValue());
        //todo create new OptionValue if not exists
        OptionValue newValue = new OptionValue();
        newValue.setOption(saleOffer.getOfferOption());
        newValue.setAlias(Transliterator.transliteration(input.getOptionValue()));
        saleOffer.setMainImage(imageRepository.createImageForObject(newValue, String.valueOf(input.getMainImage())));
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
            entityManager.remove(product);
            //todo removing images for saleOffers
        }
    }

    public void removeSaleOfferFromProduct(Long saleOfferId) {
        SaleOffer saleOffer = getSaleOfferById(saleOfferId);
        if (saleOffer != null) {
            entityManager.remove(saleOffer);
        }
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
}
