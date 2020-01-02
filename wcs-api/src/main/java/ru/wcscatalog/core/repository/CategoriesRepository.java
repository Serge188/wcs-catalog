package ru.wcscatalog.core.repository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.wcscatalog.core.dto.*;
import ru.wcscatalog.core.model.*;
import ru.wcscatalog.core.utils.Transliterator;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class CategoriesRepository {
    private final ImageRepository imageRepository;
    private final Dao dao;

    public CategoriesRepository(
            ImageRepository imageRepository,
            Dao dao) {
        this.imageRepository = imageRepository;
        this.dao = dao;
    }

    public List<CategoryEntry> getCategories() {
        List<Category> categories = dao.getAll(Category.class);
        categories.forEach(c -> {
            if (c.getOrderNumber() == null) c.setOrderNumber(0);
        });
        categories = categories.stream().sorted(Comparator.comparingInt(Category::getOrderNumber)).collect(Collectors.toList());
        return categories.stream().map(CategoryEntry::fromCategory).collect(Collectors.toList());
    }

    public List<CategoryEntry> getCategoriesWithProductCount() {
        List<CategoryEntry> entries = getCategories();
        fillProductCount(entries);
        return entries;
    }

    public Category getCategoryById(Long id) {
        return dao.byId(id, Category.class);
    }

    public CategoryEntry getCategoryByAlias(String alias) {
        Category category = dao.singleByProperty("alias", alias, Category.class);
        if (category != null) {
            CategoryEntry entry = CategoryEntry.fromCategory(category);
            if (category != null && category.getChildCategories() != null) {
                List<CategoryEntry> childCategories = new ArrayList<>();
                category.getChildCategories().forEach(e -> childCategories.add(CategoryEntry.fromCategory(e)));
                entry.setChildCategories(childCategories.stream().sorted(Comparator.comparingInt(CategoryEntry::getOrderNumber)).collect(Collectors.toList()));
            }
            return entry;
        }
        return null;
    }

    @PreAuthorize("isAuthenticated()")
    public void updateCategory(CategoryInput input) throws Exception {
        Category category = dao.byId(input.getId(), Category.class);
        category.setTitle(input.getTitle());
        category.setAlias(Transliterator.transliteration(input.getTitle()));
        if (input.getParentCategoryId() == null) {
            category.setParentCategory(null);
        } else if (!input.getParentCategoryId().equals(category.getParentCategory().getId())) {
            Category parentCategory = dao.byId(input.getParentCategoryId(), Category.class);
            category.setParentCategory(parentCategory);
        }

        category.setDescription(input.getDescription());
        category.setPopular(input.isPopular());
        if (input.isImageChanged()) {
            String data = ((String) input.getImageInput());
            Image image = imageRepository.createImageForObject(category, data);
            category.setImage(image);
        }
    }

    @PreAuthorize("isAuthenticated()")
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
            Category parentCategory = dao.byId(input.getParentCategoryId(), Category.class);
            category.setParentCategory(parentCategory);
        }
        if (input.getImageInput() != null) {
            String data = ((String) input.getImageInput());
            Image image = imageRepository.createImageForObject(category, data);
            category.setImage(image);
        }
        dao.add(category);
    }

    @PreAuthorize("isAuthenticated()")
    public void removeCategory(Long categoryId) throws Exception {
        Category category = dao.byId(categoryId, Category.class);
        try {
            imageRepository.removeImageForObject(category);
        } catch (Exception e) {
            throw new Exception("error.occurred.while.removing.category.image");
        }
        if (category.getChildCategories() != null) {
            category.getChildCategories().forEach(c -> c.setParentCategory(null));
        }
        dao.remove(category);
//        entityManager.close();
    }

    public Collection<OfferOptionEntry> getPossibleFilterOptions(Long categoryId) {
        CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
        CriteriaQuery<ProductOptionsRelation> productOptionsCriteriaQuery =
                criteriaBuilder.createQuery(ProductOptionsRelation.class);
        Root root = productOptionsCriteriaQuery.from(ProductOptionsRelation.class);
        Join productJoin = root.join("product");
        Join categoryJoin = productJoin.join("category");
        Join optionsJoin = root.join("option");
        productOptionsCriteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.equal(categoryJoin.get("id"), categoryId),
                criteriaBuilder.equal(optionsJoin.get("showInFilter"), true)
        ));
        List<ProductOptionsRelation> productOptionsRelations = dao.createQuery(productOptionsCriteriaQuery);
        Map<Long, OfferOptionEntry> optionsMap = new HashMap<>();
        productOptionsRelations.forEach(or -> {
            optionsMap.putIfAbsent(or.getOption().getId(), OfferOptionEntry.fromOfferOption(or.getOption()));
        });
        productOptionsRelations.forEach(or -> {
            if (!optionsMap.get(or.getOption().getId()).getValues().stream().map(v -> v.getValue()).collect(Collectors.toList())
                    .contains(or.getValue().getValue())) {
                optionsMap.get(or.getOption().getId()).getValues().add(OptionValueEntry.fromOptionValue(or.getValue()));
            }
        });
        return optionsMap.values();
    }

    public List<FactoryEntry> getFactoriesForCategory(long categoryId) {
        CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
        CriteriaQuery productsCriteria = criteriaBuilder.createQuery();
        Root<Product> root = productsCriteria.from(Product.class);
        Join categoryJoin = root.join("category");
        productsCriteria.where(criteriaBuilder.equal(categoryJoin.get("id"), categoryId));
        productsCriteria.select(root.get("factory"));
        productsCriteria.distinct(true);
        List<Factory> factories = dao.createQuery(productsCriteria);
        return factories.stream().map(FactoryEntry::fromFactory).collect(Collectors.toList());
    }

    protected void fillProductCount(List<CategoryEntry> entries) {
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
        CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
        entries.forEach(e -> {
            if (e.isPopular() != null && e.isPopular() && categoriesMap.containsKey(e.getId())) {
                categoriesMap.get(e.getId()).forEach(childCat -> {
                    CriteriaQuery<Product> criteriaQueryProduct = criteriaBuilder.createQuery(Product.class);
                    Root<Product> root = criteriaQueryProduct.from(Product.class);
                    criteriaQueryProduct.where(criteriaBuilder.equal(root.get("category"), childCat.getId()));
                    List<Product> p = dao.createQuery(criteriaQueryProduct);
                    Integer productsCount = dao.createQuery(criteriaQueryProduct).size();
                    childCat.setProductsCount(productsCount);
                });
            }
        });
    }

    @Transactional
    public List<CategoryEntry> getTopLevelCategories() {
        CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
        CriteriaQuery<Category> criteria = criteriaBuilder.createQuery(Category.class);
        Root<Category> root = criteria.from(Category.class);
        criteria.where(criteriaBuilder.isNull(root.get("parentCategory")));
        List<Category> categories = dao.createQuery(criteria);
        return categories.stream().map(CategoryEntry::fromCategory).collect(Collectors.toList());
    }

    @Transactional
    public void updateCategoryOrderNumber(Long categoryId, Integer orderNumber) {
        Category category = dao.byId(categoryId, Category.class);
        if (category != null) category.setOrderNumber(orderNumber);
    }
}
