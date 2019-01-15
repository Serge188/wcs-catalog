package ru.wcscatalog.core.dto;

import ru.wcscatalog.core.model.Category;

public class CategoryEntry {
    private Long id;
    private String title;
    private String alias;
    private Long parentCategoryId;
    private ImageEntry image;
    private Boolean popular;

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

    public void setImage(ImageEntry image) {
        this.image = image;
    }

    public Boolean isPopular() {
        return popular;
    }

    public void setPopular(Boolean popular) {
        this.popular = popular;
    }

    public static CategoryEntry fromCategory(Category category) {
        if (category != null) {
            CategoryEntry entry = new CategoryEntry();
            entry.setId(category.getId());
            entry.setTitle(category.getTitle());
            entry.setAlias(category.getAlias());
            if (category.getParentCategory() != null) {
                entry.setParentCategoryId(category.getParentCategory().getId());
            }
            entry.setImage(ImageEntry.fromImage(category.getImage()));
            entry.setPopular(category.isPopular());
            return entry;
        }
        return null;
    }
}
