package ru.wcscatalog.core.dto;

import java.util.ArrayList;
import java.util.List;

public class CategoryFilter {
    private Float minPrice;
    private Float maxPrice;
    private List<Long> factoryIds = new ArrayList<>();
    private List<OfferOptionInput> options = new ArrayList();

    public Float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Float minPrice) {
        this.minPrice = minPrice;
    }

    public Float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Float maxPrice) {
        this.maxPrice = maxPrice;
    }

    public List<Long> getFactoryIds() {
        return factoryIds;
    }

    public void setFactoryIds(List<Long> factoryIds) {
        this.factoryIds = factoryIds;
    }

    public List<OfferOptionInput> getOptions() {
        return options;
    }

    public void setOptions(List<OfferOptionInput> options) {
        this.options = options;
    }
}
