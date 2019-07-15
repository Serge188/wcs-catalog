package ru.wcscatalog.core.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private List<Image> images = new ArrayList<>();

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="factory_id")
    private Factory factory;

    @Column(name="price_type")
    @Enumerated(EnumType.STRING)
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

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }
}
