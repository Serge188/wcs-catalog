package ru.wcscatalog.core.dto;

import java.math.BigDecimal;

public class ProductSimplifiedEntry {
    private Long id;
    private String title;
    private String alias;
    private String imageLink;
    private BigDecimal price;
    private Integer qty;
    private BigDecimal sum;
    private Long currentSaleOfferId;

    public ProductSimplifiedEntry() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public Long getCurrentSaleOfferId() {
        return currentSaleOfferId;
    }

    public void setCurrentSaleOfferId(Long currentSaleOfferId) {
        this.currentSaleOfferId = currentSaleOfferId;
    }
}
