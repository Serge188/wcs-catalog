package ru.wcscatalog.core.dto;

public class OptionValueInput {
    private Long id;
    private Long optionId;
    private String value;
    private Object imageInput;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object getImageInput() {
        return imageInput;
    }

    public void setImageInput(Object imageInput) {
        this.imageInput = imageInput;
    }
}
