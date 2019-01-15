package ru.wcscatalog.core.dto;

import ru.wcscatalog.core.model.Factory;

public class FactoryEntry {

    private long id;
    private String title;
    private String alias;
    private ImageEntry image;
    private Boolean popular;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public ImageEntry getImage() {
        return image;
    }

    public void setImage(ImageEntry image) {
        this.image = image;
    }

    public Boolean getPopular() {
        return popular;
    }

    public void setPopular(Boolean popular) {
        this.popular = popular;
    }

    public static FactoryEntry fromFactory(Factory factory) {
        if (factory != null) {
            FactoryEntry entry = new FactoryEntry();
            entry.setId(factory.getId());
            entry.setTitle(factory.getTitle());
            entry.setAlias(factory.getAlias());
            entry.setImage(ImageEntry.fromImage(factory.getImage()));
            entry.setPopular(factory.isPopular());
            return entry;
        }
        return null;
    }
}
