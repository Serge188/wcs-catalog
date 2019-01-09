package ru.wcscatalog.core.model;

import javax.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name="main_image")
    private String mainImage;

    @Column(name="images")
    private String images;

    @Column(name="alias_name", nullable = false)
    private String alias;

    @Column(name="description")
    private String description;

    @Column(name="is_product_of_day")
    private Boolean productOfDay;

    @Column(name="is_new")
    private Boolean newProduct;

    @Column(name="is_hit")
    private Boolean hit;

    @Column(name="has_promo")
    private Boolean promo;

    @Column(name="price")
    private Float price;

    @Column(name="discount_price")
    private Float discountPrice;

    @Column(name="is_popular")
    private Boolean popular;

    protected Product() {}

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

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
