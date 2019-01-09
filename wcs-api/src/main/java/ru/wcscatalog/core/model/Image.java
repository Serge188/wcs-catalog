package ru.wcscatalog.core.model;

import javax.persistence.*;

@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @Column(name="img_lg")
    private String largeImageLink;

    @Column(name="img_md")
    private String mediumImageLink;

    @Column(name="img_sm")
    private String smallImageLink;

    @Column(name="img_preview")
    private String previewImageLink;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLargeImageLink() {
        return largeImageLink;
    }

    public void setLargeImageLink(String largeImageLink) {
        this.largeImageLink = largeImageLink;
    }

    public String getMediumImageLink() {
        return mediumImageLink;
    }

    public void setMediumImageLink(String mediumImageLink) {
        this.mediumImageLink = mediumImageLink;
    }

    public String getSmallImageLink() {
        return smallImageLink;
    }

    public void setSmallImageLink(String smallImageLink) {
        this.smallImageLink = smallImageLink;
    }

    public String getPreviewImageLink() {
        return previewImageLink;
    }

    public void setPreviewImageLink(String previewImageLink) {
        this.previewImageLink = previewImageLink;
    }
}
