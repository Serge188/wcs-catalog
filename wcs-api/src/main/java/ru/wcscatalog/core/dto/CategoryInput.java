package ru.wcscatalog.core.dto;

public class CategoryInput {
    private Long id;
    private String title;
    private Long parentCategoryId;
    private String description;
    private boolean isPopular;
    private Object imageInput;
    private boolean imageChanged;

    public CategoryInput() {
    }

    public CategoryInput(Long id, String title, Long parentCategoryId, String description, boolean isPopular) {
        this.id = id;
        this.title = title;
        this.parentCategoryId = parentCategoryId;
        this.description = description;
        this.isPopular = isPopular;
    }

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

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPopular() {
        return isPopular;
    }

    public void setPopular(boolean popular) {
        isPopular = popular;
    }

    public Object getImageInput() {
        return imageInput;
    }

    public void setImageInput(Object imageInput) {
        this.imageInput = imageInput;
    }

    public boolean isImageChanged() {
        return imageChanged;
    }

    public void setImageChanged(boolean imageChanged) {
        this.imageChanged = imageChanged;
    }
}
