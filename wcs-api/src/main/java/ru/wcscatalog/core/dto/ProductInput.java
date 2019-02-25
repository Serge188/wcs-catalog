package ru.wcscatalog.core.dto;

import ru.wcscatalog.core.model.PriceType;

import java.util.ArrayList;
import java.util.List;

public class ProductInput {
    private Long id;
    private String title;
    private Object imageInput;
    private List<Object> imagesInput = new ArrayList<>();
    private String description;
    private Boolean productOfDay;
    private Boolean newProduct;
    private Boolean hit;
    private Boolean promo;
    private Float price;
    private Float discountPrice;
    private Boolean popular;
    private Long categoryId;
    private Long factoryId;
    private List<OfferOptionInput> options = new ArrayList<>();
    private List<SaleOfferInput> saleOffers = new ArrayList<>();
    private PriceType priceType;

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

    public Object getImageInput() {
        return imageInput;
    }

    public void setImageInput(Object imageInput) {
        this.imageInput = imageInput;
    }

    public List<Object> getImagesInput() {
        return imagesInput;
    }

    public void setImagesInput(List<Object> imagesInput) {
        this.imagesInput = imagesInput;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getProductOfDay() {
        return productOfDay;
    }

    public void setProductOfDay(Boolean productOfDay) {
        this.productOfDay = productOfDay;
    }

    public Boolean getNewProduct() {
        return newProduct;
    }

    public void setNewProduct(Boolean newProduct) {
        this.newProduct = newProduct;
    }

    public Boolean getHit() {
        return hit;
    }

    public void setHit(Boolean hit) {
        this.hit = hit;
    }

    public Boolean getPromo() {
        return promo;
    }

    public void setPromo(Boolean promo) {
        this.promo = promo;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Float discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Boolean getPopular() {
        return popular;
    }

    public void setPopular(Boolean popular) {
        this.popular = popular;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public List<SaleOfferInput> getSaleOffers() {
        return saleOffers;
    }

    public List<OfferOptionInput> getOptions() {
        return options;
    }

    public void setOptions(List<OfferOptionInput> options) {
        this.options = options;
    }

    public void setSaleOffers(List<SaleOfferInput> saleOffers) {
        this.saleOffers = saleOffers;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }


}
