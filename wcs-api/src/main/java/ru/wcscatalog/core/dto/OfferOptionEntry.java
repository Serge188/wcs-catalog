package ru.wcscatalog.core.dto;

import ru.wcscatalog.core.model.OfferOption;

public class OfferOptionEntry {
    private long id;
    private String title;
    private String type;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static OfferOptionEntry fromOfferOption(OfferOption option) {
        if (option != null) {
            OfferOptionEntry entry = new OfferOptionEntry();
            entry.setId(option.getId());
            entry.setTitle(option.getTitle());
            entry.setType(option.getType());
            return entry;
        }
        return null;
    }
}
