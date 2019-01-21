package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import ru.wcscatalog.core.dto.ProductEntry;
import ru.wcscatalog.core.dto.SaleOfferEntry;
import ru.wcscatalog.core.model.Category;
import ru.wcscatalog.core.model.Product;
import ru.wcscatalog.core.model.SaleOffer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;

    public ProductRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
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
