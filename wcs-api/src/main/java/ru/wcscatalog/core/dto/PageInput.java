package ru.wcscatalog.core.dto;

public class PageInput {
    private Long id;
    private String title;
    private String alias;
    private String sliderHeader;
    private String sliderPromo;
    private String sliderAnnotation;
    private String content;
    private Object sliderImageInput;
    private Object imageInput;
    private Boolean isSlider;
    private Long parentPageId;
    private Boolean showInMainMenu;
    private Boolean showInSideMenu;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getSliderHeader() {
        return sliderHeader;
    }

    public void setSliderHeader(String sliderHeader) {
        this.sliderHeader = sliderHeader;
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

    public Object getSliderImageInput() {
        return sliderImageInput;
    }

    public void setSliderImageInput(Object sliderImageInput) {
        this.sliderImageInput = sliderImageInput;
    }

    public Object getImageInput() {
        return imageInput;
    }

    public void setImageInput(Object imageInput) {
        this.imageInput = imageInput;
    }

    public Boolean getIsSlider() {
        return isSlider;
    }

    public void setIsSlider(Boolean isSlider) {
        this.isSlider = isSlider;
    }

    public Long getParentPageId() {
        return parentPageId;
    }

    public void setParentPageId(Long parentPageId) {
        this.parentPageId = parentPageId;
    }

    public Boolean isShowInMainMenu() {
        return showInMainMenu;
    }

    public void setShowInMainMenu(Boolean showInMainMenu) {
        this.showInMainMenu = showInMainMenu;
    }

    public Boolean isShowInSideMenu() {
        return showInSideMenu;
    }

    public void setShowInSideMenu(Boolean showInSideMenu) {
        this.showInSideMenu = showInSideMenu;
    }
}
