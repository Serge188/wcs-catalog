package ru.wcscatalog.core.dto;

public class ProductOptionRelationsEntry {
    private Long id;
    private ProductEntry product;
    private OfferOptionEntry option;
    private OptionValueEntry value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductEntry getProduct() {
        return product;
    }

    public void setProduct(ProductEntry product) {
        this.product = product;
    }

    public OfferOptionEntry getOption() {
        return option;
    }

    public void setOption(OfferOptionEntry option) {
        this.option = option;
    }

    public OptionValueEntry getValue() {
        return value;
    }

    public void setValue(OptionValueEntry value) {
        this.value = value;
    }
}
