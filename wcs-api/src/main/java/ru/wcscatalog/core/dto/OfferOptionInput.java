package ru.wcscatalog.core.dto;

import java.util.List;

public class OfferOptionInput {
    private Long id;
    private String title;
    private String name;
    private List<OptionValueInput> values;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OptionValueInput> getValues() {
        return values;
    }

    public void setValues(List<OptionValueInput> values) {
        this.values = values;
    }
}
