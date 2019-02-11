package ru.wcscatalog.core.model;

import ru.wcscatalog.core.dto.SaleOfferEntry;

import javax.persistence.*;

@Entity
@Table(name = "sale_offers")
public class SaleOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="price")
    private Float price;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="main_image_id")
    private Image mainImage;

    @ManyToOne(targetEntity = Product.class)
    @JoinColumn(name="product_id")
    private Product product;

    @ManyToOne(targetEntity = OfferOption.class)
    @JoinColumn(name="option_id")
    private OfferOption offerOption;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="option_value_id")
    private OptionValue optionValue;

    @Column(name="discount_price")
    private Float discountPrice;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="button_img_id")
    private Image buttonImage;

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

    public Image getMainImage() {
        return mainImage;
    }

    public void setMainImage(Image mainImage) {
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

    public OptionValue getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(OptionValue optionValue) {
        this.optionValue = optionValue;
    }

    public Float getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Float discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Image getButtonImage() {
        return buttonImage;
    }

    public void setButtonImage(Image buttonImage) {
        this.buttonImage = buttonImage;
    }
}
