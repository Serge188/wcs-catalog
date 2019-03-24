package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.wcscatalog.core.dto.*;
import ru.wcscatalog.core.model.*;
import ru.wcscatalog.core.utils.Transliterator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CategoriesRepository {
    private final ImageRepository imageRepository;
    private final EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<Category> criteriaQuery;
    private Root<Category> root;

    public CategoriesRepository(EntityManagerFactory entityManagerFactory, ImageRepository imageRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.imageRepository = imageRepository;
        buildCriteriaQuery();
    }

    @Transactional
    public List<CategoryEntry> getCategories() {
        criteriaQuery = criteriaBuilder.createQuery(Category.class);
        root = criteriaQuery.from(Category.class);
        Query query = entityManager.createQuery(criteriaQuery);
        List<Category> categories = query.getResultList();
        List<CategoryEntry> entries = categories.stream().map(CategoryEntry::fromCategory).collect(Collectors.toList());
        return entries;
    }

    @Transactional
    public List<CategoryEntry> getCategoriesWithProductCount() {
        List<CategoryEntry> entries = getCategories();
        fillProductCount(entries);
        return entries;
    }

    public Category getCategoryById(Long id) {
        criteriaQuery = criteriaBuilder.createQuery(Category.class);
        root = criteriaQuery.from(Category.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));
        Optional<Category> category = entityManager.createQuery(criteriaQuery).getResultList().stream().findFirst();
        return category.orElse(null);
    }

    public CategoryEntry getCategoryByAlias(String alias) {
        criteriaQuery = criteriaBuilder.createQuery(Category.class);
        root = criteriaQuery.from(Category.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("alias"), alias));
        Optional<Category> category = entityManager.createQuery(criteriaQuery).getResultList().stream().findFirst();
        if (category.isPresent()) {
            CategoryEntry entry = CategoryEntry.fromCategory(category.get());
            category.get().getChildCategories().forEach(e -> entry.getChildCategories().add(CategoryEntry.fromCategory(e)));
            return entry;
        }
        return null;
    }

    @Transactional
    public void updateCategory(CategoryInput input) throws Exception {
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), input.getId()));
        Category category = entityManager.createQuery(criteriaQuery).getSingleResult();
        category.setTitle(input.getTitle());
        category.setAlias(Transliterator.transliteration(input.getTitle()));
        if (input.getParentCategoryId() == null) {
            category.setParentCategory(null);
        } else if (!input.getParentCategoryId().equals(category.getParentCategory().getId())) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("id"), input.getParentCategoryId()));
            Category parentCategory = entityManager.createQuery(criteriaQuery).getSingleResult();
            category.setParentCategory(parentCategory);
        }
        category.setDescription(input.getDescription());
        category.setPopular(input.isPopular());
        if (input.isImageChanged()) {
            String data = ((String) input.getImageInput());
            Image image = imageRepository.createImageForObject(category, data);
            category.setImage(image);
        }
        entityManager.getTransaction().begin();
        entityManager.persist(category);
        entityManager.getTransaction().commit();
    }

    @Transactional
    public void createCategory(CategoryInput input) throws Exception {
        Category category = new Category();
        category.setTitle(input.getTitle());
        String alias = Transliterator.transliteration(input.getTitle());
        boolean aliasIsBusy = true;
        int count = 0;
        while (aliasIsBusy) {
            CategoryEntry categoryWithSameAlias = getCategoryByAlias(alias);
            if (categoryWithSameAlias != null) {
                alias = alias + String.valueOf(count++);
            } else {
                aliasIsBusy = false;
            }
        }
        category.setAlias(alias);
        category.setDescription(input.getDescription());
        category.setPopular(input.isPopular());
        if (input.getParentCategoryId() != null) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("id"), input.getParentCategoryId()));
            Category parentCategory = entityManager.createQuery(criteriaQuery).getSingleResult();
            category.setParentCategory(parentCategory);
        }
        if (input.getImageInput() != null) {
            String data = ((String) input.getImageInput());
            Image image = imageRepository.createImageForObject(category, data);
            category.setImage(image);
        }
        entityManager.getTransaction().begin();
        entityManager.persist(category);
        entityManager.getTransaction().commit();
    }

    @Transactional
    public void removeCategory(Long categoryId) throws Exception {
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), categoryId));
        Category category = entityManager.createQuery(criteriaQuery).getSingleResult();
        try {
            imageRepository.removeImageForObject(category);
            entityManager.getTransaction().begin();
            entityManager.remove(category);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            throw new Exception("error.occurred.while.removing.category");
        }
    }

    @Transactional
    public Collection<OfferOptionEntry> getPossibleFilterOptions(Long categoryId) {
        CriteriaQuery<ProductOptionsRelation> productOptionsCriteriaQuery =
                criteriaBuilder.createQuery(ProductOptionsRelation.class);
        Root root = productOptionsCriteriaQuery.from(ProductOptionsRelation.class);
        Join productJoin = root.join("product");
        Join categoryJoin = productJoin.join("category");
        criteriaQuery.where(criteriaBuilder.equal(categoryJoin.get("id"), categoryId));
        List<ProductOptionsRelation> productOptionsRelations = entityManager.createQuery(productOptionsCriteriaQuery).getResultList();
        Map<Long, OfferOptionEntry> optionsMap = new HashMap<>();
        productOptionsRelations.forEach(or -> {
            optionsMap.putIfAbsent(or.getOption().getId(), OfferOptionEntry.fromOfferOption(or.getOption()));
        });
        productOptionsRelations.forEach(or -> {
            optionsMap.get(or.getOption().getId()).getValues().add(OptionValueEntry.fromOptionValue(or.getValue()));
        });
        return optionsMap.values();
    }

    @Transactional
    public List<Float> getPricesRange(long categoryId) {
        CriteriaQuery productsCriteria = criteriaBuilder.createQuery();
        Root<Product> root = productsCriteria.from(Product.class);
        Join categoryJoin = root.join("category");
        productsCriteria.where(criteriaBuilder.equal(categoryJoin.get("id"), categoryId));
        productsCriteria.select(root.get("price"));
        List<Float> prices = entityManager.createQuery(productsCriteria).getResultList();
        List<Float> minMaxPrices = new ArrayList<>();
        prices.stream().max(Comparator.comparing(Float::valueOf)).ifPresent(minMaxPrices::add);
        prices.stream().min(Comparator.comparing(Float::valueOf)).ifPresent(minMaxPrices::add);
        return minMaxPrices;
    }

    @Transactional
    public List<FactoryEntry> getFactoriesForCategory(long categoryId) {
        CriteriaQuery productsCriteria = criteriaBuilder.createQuery();
        Root<Product> root = productsCriteria.from(Product.class);
        Join categoryJoin = root.join("category");
        productsCriteria.where(criteriaBuilder.equal(categoryJoin.get("id"), categoryId));
        productsCriteria.select(root.get("factory"));
        productsCriteria.distinct(true);
        List<Factory> factories = entityManager.createQuery(productsCriteria).getResultList();
        return factories.stream().map(FactoryEntry::fromFactory).collect(Collectors.toList());
    }

    private void buildCriteriaQuery() {
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
        }
        criteriaBuilder = entityManager.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(Category.class);
        root = criteriaQuery.from(Category.class);
    }

    private void fillProductCount(List<CategoryEntry> entries) {
        Map<Long, List<CategoryEntry>> categoriesMap = new HashMap<>();
        entries.forEach(e -> {
            if (e.getParentCategoryId() == null) {
                categoriesMap.put(e.getId(), new ArrayList<>());
            }
        });
        for (CategoryEntry entry : entries) {
            if (entry.getParentCategoryId() != null) {
                categoriesMap.putIfAbsent(entry.getParentCategoryId(), new ArrayList<>());
                categoriesMap.get(entry.getParentCategoryId()).add(entry);
            }
        }
        entries.forEach(e -> {
            if (e.isPopular() != null && e.isPopular() && categoriesMap.containsKey(e.getId())) {
                categoriesMap.get(e.getId()).forEach(childCat -> {
                    CriteriaQuery<Product> criteriaQueryProduct = criteriaBuilder.createQuery(Product.class);
                    Root<Product> root = criteriaQueryProduct.from(Product.class);
                    criteriaQueryProduct.where(criteriaBuilder.equal(root.get("category"), childCat.getId()));
                    List<Product> p = entityManager.createQuery(criteriaQueryProduct).getResultList();
                    Integer productsCount = entityManager.createQuery(criteriaQueryProduct).getResultList().size();
                    childCat.setProductsCount(productsCount);
                });
            }
        });
    }
}
