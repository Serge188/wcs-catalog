package ru.wcscatalog.core.dto;

import ru.wcscatalog.core.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductEntry {
    private Long id;
    private String title;
    private String mainImage;
    private List<String> images;
    private String alias;
    private String descritpion;
    private Boolean productOfDay;
    private Boolean newProduct;
    private Boolean hit;
    private Boolean promo;
    private Float price;
    private Float discountPrice;
    private Boolean popular;
    private String altTitle;
    private String shortDescription;

    private List<SaleOfferEntry> saleOffers;

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

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescritpion() {
        return descritpion;
    }

    public void setDescritpion(String descritpion) {
        this.descritpion = descritpion;
    }

    public Boolean isProductOfDay() {
        return productOfDay;
    }

    public void setProductOfDay(Boolean productOfDay) {
        this.productOfDay = productOfDay;
    }

    public Boolean isNewProduct() {
        return newProduct;
    }

    public void setNewProduct(Boolean newProduct) {
        this.newProduct = newProduct;
    }

    public Boolean isHit() {
        return hit;
    }

    public void setHit(Boolean hit) {
        this.hit = hit;
    }

    public Boolean isPromo() {
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

    public Boolean isPopular() {
        return popular;
    }

    public void setPopular(Boolean popular) {
        this.popular = popular;
    }

    public List<SaleOfferEntry> getSaleOffers() {
        return saleOffers;
    }

    public void setSaleOffers(List<SaleOfferEntry> saleOffers) {
        this.saleOffers = saleOffers;
    }

    public String getAltTitle() {
        return altTitle;
    }

    public void setAltTitle(String altTitle) {
        this.altTitle = altTitle;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public static ProductEntry fromProduct(Product product) {
        if (product != null) {
            ProductEntry entry = new ProductEntry();
            entry.setId(product.getId());
            entry.setTitle(product.getTitle());
            entry.setMainImage(product.getMainImage());
            List<String> imagesList = new ArrayList<>();
            if (imagesList != null) {
                if (imagesList.contains(";")) {
                    Collections.addAll(imagesList, product.getImages().split(";"));
                } else {
                    imagesList.add(product.getImages());
                }
            }
            entry.setImages(imagesList);
            entry.setAlias(product.getAlias());
            entry.setDescritpion(product.getDescription());
            entry.setProductOfDay(product.isProductOfDay());
            entry.setNewProduct(product.isNewProduct());
            entry.setHit(product.isHit());
            entry.setPromo(product.isPromo());
            entry.setPrice(product.getPrice());
            entry.setDiscountPrice(product.getDiscountPrice());
            entry.setPopular(product.isPopular());
            entry.setAltTitle(product.getTitle().replace("\"", "&quot;"));
            if (product.getDescription().length() > 163) {
                entry.setShortDescription(product.getDescription().substring(0, 159) + "...");
            } else {
                entry.setShortDescription(product.getDescription());
            }
            return entry;
        }
        return null;
    }
}
