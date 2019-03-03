package ru.wcscatalog.core.dto;

import ru.wcscatalog.core.model.Category;

import java.util.List;

public class CategoryEntry {
    private Long id;
    private String title;
    private String alias;
    private Long parentCategoryId;
    private String parentCategoryTitle;
    private String parentCategoryAlias;
    private ImageEntry image;
    private Boolean popular;
    private String description;
    private List<CategoryEntry> childCategories;
    private Integer productsCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public ImageEntry getImage() {
        return image;
    }

    public String getParentCategoryTitle() {
        return parentCategoryTitle;
    }

    public void setParentCategoryTitle(String parentCategoryTitle) {
        this.parentCategoryTitle = parentCategoryTitle;
    }

    public String getParentCategoryAlias() {
        return parentCategoryAlias;
    }

    public void setParentCategoryAlias(String parentCategoryAlias) {
        this.parentCategoryAlias = parentCategoryAlias;
    }

    public void setImage(ImageEntry image) {
        this.image = image;
    }

    public Boolean isPopular() {
        return popular;
    }

    public void setPopular(Boolean popular) {
        this.popular = popular;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CategoryEntry> getChildCategories() {
        return childCategories;
    }

    public void setChildCategories(List<CategoryEntry> childCategories) {
        this.childCategories = childCategories;
    }

    public Integer getProductsCount() {
        return productsCount;
    }

    public void setProductsCount(Integer productsCount) {
        this.productsCount = productsCount;
    }

    public static CategoryEntry fromCategory(Category category) {
        if (category != null) {
            CategoryEntry entry = new CategoryEntry();
            entry.setId(category.getId());
            entry.setTitle(category.getTitle());
            entry.setAlias(category.getAlias());
            if (category.getParentCategory() != null) {
                entry.setParentCategoryId(category.getParentCategory().getId());
                entry.setParentCategoryTitle(category.getParentCategory().getTitle());
                entry.setParentCategoryAlias(category.getParentCategory().getAlias());
            }
            entry.setImage(ImageEntry.fromImage(category.getImage()));
            entry.setPopular(category.isPopular());
            entry.setDescription(category.getDescription());
            return entry;
        }
        return null;
    }
}
