package ru.wcscatalog.core.model;

import javax.persistence.*;

@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @Column(name="img_original")
    private String originalImageLink;

    @Column(name="img_base")
    private String baseImageLink;

    @Column(name="img_card")
    private String cardImageLink;

    @Column(name="img_gallery")
    private String galleryImageLink;

    @Column(name="img_preview")
    private String previewImageLink;

    @Column(name="img_option")
    private String optionImageLink;

    @Column(name="img_category")
    private String categoryImageLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOriginalImageLink() {
        return originalImageLink;
    }

    public void setOriginalImageLink(String originalImageLink) {
        this.originalImageLink = originalImageLink;
    }

    public String getBaseImageLink() {
        return baseImageLink;
    }

    public void setBaseImageLink(String baseImageLink) {
        this.baseImageLink = baseImageLink;
    }

    public String getCardImageLink() {
        return cardImageLink;
    }

    public void setCardImageLink(String cardImageLink) {
        this.cardImageLink = cardImageLink;
    }

    public String getGalleryImageLink() {
        return galleryImageLink;
    }

    public void setGalleryImageLink(String galleryImageLink) {
        this.galleryImageLink = galleryImageLink;
    }

    public String getPreviewImageLink() {
        return previewImageLink;
    }

    public void setPreviewImageLink(String previewImageLink) {
        this.previewImageLink = previewImageLink;
    }

    public String getOptionImageLink() {
        return optionImageLink;
    }

    public void setOptionImageLink(String optionImageLink) {
        this.optionImageLink = optionImageLink;
    }

    public String getCategoryImageLink() {
        return categoryImageLink;
    }

    public void setCategoryImageLink(String categoryImageLink) {
        this.categoryImageLink = categoryImageLink;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
