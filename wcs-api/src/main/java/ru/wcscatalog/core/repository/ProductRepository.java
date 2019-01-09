package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import ru.wcscatalog.core.dto.ProductEntry;
import ru.wcscatalog.core.model.Category;
import ru.wcscatalog.core.model.Product;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<Product> criteriaQuery;
    private Root<Product> root;

    public ProductRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        buildCriteriaQuery();
    }

    public List<ProductEntry> getPopularProducts() {
//        if (entityManager == null) {
//            entityManager = entityManagerFactory.createEntityManager();
//        }
//        criteriaBuilder = entityManager.getCriteriaBuilder();
//        criteriaQuery = criteriaBuilder.createQuery(Product.class);
//        root = criteriaQuery.from(Product.class);
//        criteriaQuery.where(criteriaBuilder.equal(root.get("popular"), true));
        Query query = entityManager.createQuery(criteriaQuery);
        List<Product> products = query.getResultList();
        List<ProductEntry> entries = new ArrayList<>();
        for (Product p: products) {
            entries.add(ProductEntry.fromProduct(p));
        }
        return entries;
    }

    private void buildCriteriaQuery() {
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
        }
        criteriaBuilder = entityManager.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(Product.class);
        root = criteriaQuery.from(Product.class);
    }
}
