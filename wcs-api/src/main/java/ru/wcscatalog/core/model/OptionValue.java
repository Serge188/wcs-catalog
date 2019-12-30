package ru.wcscatalog.core.model;

import javax.persistence.*;

@Entity
@Table(name = "option_values")
public class OptionValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "option_id")
    private OfferOption option;

    @Column(name="value")
    private String value;

    @Column(name="alias")
    private String alias;

    @Column(name="order_number")
    private Integer orderNumber = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="image_id")
    private Image image;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OfferOption getOption() {
        return option;
    }

    public void setOption(OfferOption option) {
        this.option = option;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }
}
