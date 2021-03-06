package ru.wcscatalog.core.dto;

public class ProductSearchEntry {
    private String title;
    private String alias;

    public ProductSearchEntry(String title, String alias) {
        this.title = title;
        this.alias = alias;
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
}
