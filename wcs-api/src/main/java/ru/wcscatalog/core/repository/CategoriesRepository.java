package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import ru.wcscatalog.core.dto.CategoryEntry;
import ru.wcscatalog.core.model.Category;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CategoriesRepository {
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<Category> criteriaQuery;
    private Root<Category> root;

    public CategoriesRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        buildCriteriaQuery();
    }

    public List<CategoryEntry> getCategories() {
        Query query = entityManager.createQuery(criteriaQuery);
        List<Category> categories = query.getResultList();
        List<CategoryEntry> entries = categories.stream().map(CategoryEntry::fromCategory).collect(Collectors.toList());
        return entries;
    }

    public CategoryEntry getCategoryByAlias(String alias) {
        List<CategoryEntry> allCategories = getCategories();
        Optional<CategoryEntry> entry = allCategories
                .stream()
                .filter(x -> x.getAlias().equals(alias))
                .findAny();
        entry.ifPresent(e -> e.setChildCategories(allCategories
                .stream()
                .filter(x -> e.getId().equals(x.getParentCategoryId()))
                .collect(Collectors.toList())));
        return entry.orElse(null);
    }

    private void buildCriteriaQuery() {
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
        }
        criteriaBuilder = entityManager.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(Category.class);
        root = criteriaQuery.from(Category.class);
    }
}
