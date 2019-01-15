package ru.wcscatalog.core.model;

import javax.persistence.*;

@Entity
@Table(name = "factories")
public class Factory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "alias")
    private String alias;

    @Column(name="is_popular")
    private Boolean popular;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="image_id")
    private Image image;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Boolean isPopular() {
        return popular;
    }

    public void setPopular(Boolean popular) {
        this.popular = popular;
    }
}
