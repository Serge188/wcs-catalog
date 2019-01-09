package ru.wcscatalog.core.dto;

import ru.wcscatalog.core.model.OfferOption;
import ru.wcscatalog.core.model.Product;
import ru.wcscatalog.core.model.SaleOffer;

import javax.persistence.Column;
import javax.persistence.ManyToOne;

public class SaleOfferEntry {
    private long id;
    private float price;
    private String mainImage;
    private long productId;
    private OfferOptionEntry offerOption;
    private String optionValue;
    private float discountPrice;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
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

    public float getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(float discountPrice) {
        this.discountPrice = discountPrice;
    }

    public static SaleOfferEntry fromSaleOffer(SaleOffer offer) {
        if (offer != null) {
            SaleOfferEntry entry = new SaleOfferEntry();
            entry.setId(offer.getId());
            entry.setPrice(offer.getPrice());
            entry.setMainImage(offer.getMainImage());
            entry.setProductId(offer.getProduct().getId());
            entry.setOfferOption(OfferOptionEntry.fromOfferOption(offer.getOfferOption()));
            entry.setOptionValue(offer.getOptionValue());
            entry.setDiscountPrice(offer.getDiscountPrice());
            return entry;
        }
        return null;
    }
}
