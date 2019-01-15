package ru.wcscatalog.core.dto;

import ru.wcscatalog.core.model.OfferOption;
import ru.wcscatalog.core.model.Product;
import ru.wcscatalog.core.model.SaleOffer;

import javax.persistence.Column;
import javax.persistence.ManyToOne;

public class SaleOfferEntry {
    private Long id;
    private Float price;
    private ImageEntry mainImage;
    private Long productId;
    private OfferOptionEntry offerOption;
    private String optionValue;
    private Float discountPrice;
    private ImageEntry buttonImage;

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

    public ImageEntry getMainImage() {
        return mainImage;
    }

    public void setMainImage(ImageEntry mainImage) {
        this.mainImage = mainImage;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public OfferOptionEntry getOfferOption() {
        return offerOption;
    }

    public void setOfferOption(OfferOptionEntry offerOption) {
        this.offerOption = offerOption;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public Float getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Float discountPrice) {
        this.discountPrice = discountPrice;
    }

    public ImageEntry getButtonImage() {
        return buttonImage;
    }

    public void setButtonImage(ImageEntry buttonImage) {
        this.buttonImage = buttonImage;
    }

    public static SaleOfferEntry fromSaleOffer(SaleOffer offer) {
        if (offer != null) {
            SaleOfferEntry entry = new SaleOfferEntry();
            entry.setId(offer.getId());
            entry.setPrice(offer.getPrice());
            entry.setMainImage(ImageEntry.fromImage(offer.getMainImage()));
            entry.setProductId(offer.getProduct().getId());
            entry.setOfferOption(OfferOptionEntry.fromOfferOption(offer.getOfferOption()));
            entry.setOptionValue(offer.getOptionValue());
            entry.setDiscountPrice(offer.getDiscountPrice());
            entry.setButtonImage(ImageEntry.fromImage(offer.getButtonImage()));
            return entry;
        }
        return null;
    }
}
