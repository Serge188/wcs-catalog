package ru.wcscatalog.core.model;

import javax.persistence.*;

@Entity
@Table(name = "product_option_relations")
public class ProductOptionsRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="option_id")
    private OfferOption option;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="option_value_id")
    private OptionValue value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public OfferOption getOption() {
        return option;
    }

    public void setOption(OfferOption option) {
        this.option = option;
    }

    public OptionValue getValue() {
        return value;
    }

    public void setValue(OptionValue value) {
        this.value = value;
    }
}
