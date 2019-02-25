package ru.wcscatalog.core.dto;

public class FactoryInput {
    private Long id;
    private String title;
    private String alias;
    private Object imageInput;
    private Boolean popular;
    private String description;

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

    public Object getImageInput() {
        return imageInput;
    }

    public void setImageInput(Object imageInput) {
        this.imageInput = imageInput;
    }

    public Boolean getPopular() {
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
}
