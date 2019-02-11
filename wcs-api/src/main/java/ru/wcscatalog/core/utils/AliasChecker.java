package ru.wcscatalog.core.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
public class AliasChecker {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;


    public AliasChecker(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
        }
        criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public String findUniqueAliasForEntity(Class clazz, String title) {
        String alias = Transliterator.transliteration(title);
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
        }
        criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<?> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<?> root = criteriaQuery.from(clazz);

        boolean aliasIsUnique = false;
        int count = 0;
        while (!aliasIsUnique) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("alias"), alias));
            List<?> entitiesWithSameALias = entityManager.createQuery(criteriaQuery).getResultList();
            if (!entitiesWithSameALias.isEmpty()) {
                alias = alias + String.valueOf(count++);
            } else {
                aliasIsUnique = true;
            }
        }
        return alias;
    }
}
