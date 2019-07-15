package ru.wcscatalog.core.repository;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
public class Dao {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public <T> List<T> getAll(@NonNull Class<T> clazz) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Transactional
    public <T> T byId(@NonNull Long id, @NonNull Class<T> clazz) {
        return singleByProperty("id", id, clazz);
    }

    @Transactional
    public <T> T singleByProperty(@NonNull String propertyName, @NonNull Object value, @NonNull Class<T> clazz) {
        return listByProperty(propertyName, value, clazz).stream().findFirst().orElse(null);
    }

    @Transactional
    public <T> List<T> listByProperty(@NonNull String propertyName, @NonNull Object value, @NonNull Class<T> clazz) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);
        criteriaQuery.where(criteriaBuilder.equal(root.get(propertyName), value));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Transactional
    public <T> void add(T entity) {
        if (entityManager.contains(entity)) {
            entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
    }

    @Transactional
    public <T> void remove(T entity) {
        if (!entityManager.contains(entity)) {
            entityManager.merge(entity);
        }
        entityManager.remove(entity);
    }

    @Transactional
    public <T> List<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }
}
