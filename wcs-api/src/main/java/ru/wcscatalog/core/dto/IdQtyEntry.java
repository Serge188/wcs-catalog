package ru.wcscatalog.core.dto;

public class IdQtyEntry extends IdToIdEntry {
    private Integer qty;
    private Float sum;

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Float getSum() {
        return sum;
    }

    public void setSum(Float sum) {
        this.sum = sum;
    }
}
