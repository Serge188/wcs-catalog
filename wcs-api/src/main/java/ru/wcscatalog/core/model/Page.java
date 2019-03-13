package ru.wcscatalog.core.model;

import javax.persistence.*;

@Entity
@Table(name = "pages")
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "alias")
    private String alias;

    @Column(name = "slider_header")
    private String sliderHeader;

    @Column(name = "slider_promo")
    private String sliderPromo;

    @Column(name = "slider_annotation")
    private String sliderAnnotation;

    @Column(name = "content", length = 2000)
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slider_image_id")
    private Image sliderImage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_page_id")
    private Page parentPage;

    @Column(name = "show_in_main_menu")
    private Boolean showInMainMenu;

    @Column(name = "show_in_side_menu")
    private Boolean showInSideMenu;

    @Column(name = "is_slider", nullable = false)
    private boolean slider;

    public Page() {
    }

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

    public String getSliederHeader() {
        return sliderHeader;
    }

    public void setSliederHeader(String sliederHeader) {
        this.sliderHeader = sliederHeader;
    }

    public String getSliderPromo() {
        return sliderPromo;
    }

    public void setSliderPromo(String sliderPromo) {
        this.sliderPromo = sliderPromo;
    }

    public String getSliderAnnotation() {
        return sliderAnnotation;
    }

    public void setSliderAnnotation(String sliderAnnotation) {
        this.sliderAnnotation = sliderAnnotation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Image getSliderImage() {
        return sliderImage;
    }

    public void setSliderImage(Image sliderImage) {
        this.sliderImage = sliderImage;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getSliderHeader() {
        return sliderHeader;
    }

    public void setSliderHeader(String sliderHeader) {
        this.sliderHeader = sliderHeader;
    }

    public boolean isSlider() {
        return slider;
    }

    public void setSlider(Boolean slider) {
        if (slider == null) {
            this.slider = false;
        }
        this.slider = slider;
    }

    public Page getParentPage() {
        return parentPage;
    }

    public void setParentPage(Page parentPage) {
        this.parentPage = parentPage;
    }

    public Boolean getShowInMainMenu() {
        return showInMainMenu;
    }

    public void setShowInMainMenu(Boolean showInMainMenu) {
        this.showInMainMenu = showInMainMenu;
    }

    public Boolean getShowInSideMenu() {
        return showInSideMenu;
    }

    public void setShowInSideMenu(Boolean showInSideMenu) {
        this.showInSideMenu = showInSideMenu;
    }
}
