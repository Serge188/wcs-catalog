package ru.wcscatalog.core.dto;

import ru.wcscatalog.core.model.OfferOption;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OfferOptionEntry {
    private long id;
    private String title;
    private String name;
    private String type;
    private List<OptionValueEntry> values = new ArrayList<>();

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<OptionValueEntry> getValues() {
        return values;
    }

    public void setValues(List<OptionValueEntry> values) {
        this.values = values;
    }

    public static OfferOptionEntry fromOfferOption(OfferOption option) {
        if (option != null) {
            OfferOptionEntry entry = new OfferOptionEntry();
            entry.setId(option.getId());
            entry.setTitle(option.getTitle());
            entry.setName(option.getName());
            entry.setType(option.getType());
//            entry.setValues(option.getValues().stream().map(OptionValueEntry::fromOptionValue).collect(Collectors.toList()));
            return entry;
        }
        return null;
    }
}
