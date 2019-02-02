package ru.wcscatalog.core.dto;

import ru.wcscatalog.core.model.OptionValue;

public class OptionValueEntry {
    private Long id;
    private OfferOptionEntry option;
    private String value;
    private String alias;
    private ImageEntry image;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OfferOptionEntry getOption() {
        return option;
    }

    public void setOption(OfferOptionEntry option) {
        this.option = option;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public static OptionValueEntry fromOptionValue(OptionValue optionValue) {
        OptionValueEntry entry = new OptionValueEntry();
        entry.setId(optionValue.getId());
        entry.setOption(OfferOptionEntry.fromOfferOption(optionValue.getOption()));
        entry.setValue(optionValue.getValue());
        entry.setAlias(optionValue.getAlias());
        entry.setImage(ImageEntry.fromImage(optionValue.getImage()));
        return entry;
    }
}
