package ru.wcscatalog.core.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "offer_options")
public class OfferOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @Column(name="title")
    private String title;

    @Column(name="name")
    private String name;

    @Column(name="type")
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id")
    private Category category;

    @Column(name="show_in_filter")
    private Boolean showInFilter = false;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "option", orphanRemoval = true)
    private List<OptionValue> values = new ArrayList<>();

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<OptionValue> getValues() {
        return values;
    }

    public void setValues(List<OptionValue> values) {
        this.values = values;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Boolean getShowInFilter() {
        return showInFilter != null && showInFilter;
    }

    public void setShowInFilter(Boolean showInFilter) {
        this.showInFilter = showInFilter;
    }
}
