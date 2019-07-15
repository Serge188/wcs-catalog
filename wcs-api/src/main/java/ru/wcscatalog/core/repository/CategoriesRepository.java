package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.wcscatalog.core.dto.*;
import ru.wcscatalog.core.model.*;
import ru.wcscatalog.core.utils.Transliterator;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CategoriesRepository {
    private final ImageRepository imageRepository;
    private final Dao dao;

    public CategoriesRepository(ImageRepository imageRepository, Dao dao) {
        this.imageRepository = imageRepository;
        this.dao = dao;
    }

    @Transactional
    public List<CategoryEntry> getCategories() {
        List<Category> categories = dao.getAll(Category.class);
        return categories.stream().map(CategoryEntry::fromCategory).collect(Collectors.toList());
    }

    @Transactional
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
            category.getChildCategories().forEach(e -> entry.getChildCategories().add(CategoryEntry.fromCategory(e)));
            return entry;
        }
        return null;
    }

    @Transactional
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

    @Transactional
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

    @Transactional
    public Collection<OfferOptionEntry> getPossibleFilterOptions(Long categoryId) {
        CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
        CriteriaQuery<ProductOptionsRelation> productOptionsCriteriaQuery =
                criteriaBuilder.createQuery(ProductOptionsRelation.class);
        Root root = productOptionsCriteriaQuery.from(ProductOptionsRelation.class);
        Join productJoin = root.join("product");
        Join categoryJoin = productJoin.join("category");
        productOptionsCriteriaQuery.where(criteriaBuilder.equal(categoryJoin.get("id"), categoryId));
        List<ProductOptionsRelation> productOptionsRelations = dao.createQuery(productOptionsCriteriaQuery);
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
        CriteriaBuilder criteriaBuilder = dao.getCriteriaBuilder();
        CriteriaQuery productsCriteria = criteriaBuilder.createQuery();
        Root<Product> root = productsCriteria.from(Product.class);
        Join categoryJoin = root.join("category");
        productsCriteria.where(criteriaBuilder.equal(categoryJoin.get("id"), categoryId));
        productsCriteria.select(root.get("price"));
        List<Float> prices = dao.createQuery(productsCriteria);
        List<Float> minMaxPrices = new ArrayList<>();
        prices.stream().max(Comparator.comparing(Float::valueOf)).ifPresent(minMaxPrices::add);
        prices.stream().min(Comparator.comparing(Float::valueOf)).ifPresent(minMaxPrices::add);
        return minMaxPrices;
    }

    @Transactional
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

    @Transactional
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
}
