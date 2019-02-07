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

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "option", fetch = FetchType.LAZY)
//    private List<OptionValue> values = new ArrayList<>();

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

//    public List<OptionValue> getValues() {
//        return values;
//    }
//
//    public void setValues(List<OptionValue> values) {
//        this.values = values;
//    }
}
