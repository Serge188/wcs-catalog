package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import ru.wcscatalog.core.dto.FactoryEntry;
import ru.wcscatalog.core.model.Factory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FactoryRepository {
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;

    public FactoryRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        initializeCriteriaBuilder();
    }

    private void initializeCriteriaBuilder() {
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
        }
        criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public List<FactoryEntry> getPopularBrands() {
        CriteriaQuery<Factory> criteriaQuery = criteriaBuilder.createQuery(Factory.class);
        Root<Factory> root = criteriaQuery.from(Factory.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("popular"), true));
        Query query = entityManager.createQuery(criteriaQuery);
        List<Factory> factories = query.getResultList();
        List<FactoryEntry> entries = new ArrayList<>();
        for (Factory p: factories) {
            entries.add(FactoryEntry.fromFactory(p));
        }
        return entries;
    }
}
