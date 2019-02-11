package ru.wcscatalog.core.dto;

public class SaleOfferInput {
    private Long id;
    private Float price;
    private Object mainImage;
    private Long productId;
    private OfferOptionInput offerOption;
    private OptionValueInput optionValue;
    private Float discountPrice;
    private Object buttonImage;
    private Object imageInput;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Object getMainImage() {
        return mainImage;
    }

    public void setMainImage(Object mainImage) {
        this.mainImage = mainImage;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public OfferOptionInput getOfferOption() {
        return offerOption;
    }

    public void setOfferOption(OfferOptionInput offerOption) {
        this.offerOption = offerOption;
    }

    public OptionValueInput getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(OptionValueInput optionValue) {
        this.optionValue = optionValue;
    }

    public Float getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Float discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Object getButtonImage() {
        return buttonImage;
    }

    public void setButtonImage(Object buttonImage) {
        this.buttonImage = buttonImage;
    }

    public Object getImageInput() {
        return imageInput;
    }

    public void setImageInput(Object imageInput) {
        this.imageInput = imageInput;
    }
}
