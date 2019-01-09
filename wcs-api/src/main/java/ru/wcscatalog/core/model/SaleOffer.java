package ru.wcscatalog.core.model;

import ru.wcscatalog.core.dto.SaleOfferEntry;

import javax.persistence.*;

@Entity
@Table(name = "sale_offers")
public class SaleOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @Column(name="price")
    private float price;

    @Column(name="main_image")
    private String mainImage;

    @ManyToOne(targetEntity = Product.class)
    @JoinColumn(name="product_id")
    private Product product;

    @ManyToOne(targetEntity = OfferOption.class)
    @JoinColumn(name="option_id")
    private OfferOption offerOption;

    @Column(name="option_value")
    private String optionValue;

    @Column(name="discount_price")
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public OfferOption getOfferOption() {
        return offerOption;
    }

    public void setOfferOption(OfferOption offerOption) {
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
}
