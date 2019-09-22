package ru.wcscatalog.core.dto;

import ru.wcscatalog.core.model.Page;

import java.util.Base64;
import java.util.List;

public class PageEntry {
    private long id;
    private String title;
    private String alias;
    private String sliderHeader;
    private String sliderPromo;
    private String sliderAnnotation;
    private String content;
    private ImageEntry sliderImage;
    private ImageEntry image;
    private boolean isSlider;
    private long parentPageId;
    private String parentPageTitle;
    private Boolean showInMainMenu;
    private Boolean showInSideMenu;
    private List<PageEntry> childPages;

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

    public ImageEntry getSliderImage() {
        return sliderImage;
    }

    public void setSliderImage(ImageEntry sliderImage) {
        this.sliderImage = sliderImage;
    }

    public ImageEntry getImage() {
        return image;
    }

    public void setImage(ImageEntry image) {
        this.image = image;
    }

    public boolean isSlider() {
        return isSlider;
    }

    public void setSlider(boolean slider) {
        this.isSlider = slider;
    }

    public long getParentPageId() {
        return parentPageId;
    }

    public void setParentPageId(long parentPageId) {
        this.parentPageId = parentPageId;
    }

    public String getParentPageTitle() {
        return parentPageTitle;
    }

    public void setParentPageTitle(String parentPageTitle) {
        this.parentPageTitle = parentPageTitle;
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

    public List<PageEntry> getChildPages() {
        return childPages;
    }

    public void setChildPages(List<PageEntry> childPages) {
        this.childPages = childPages;
    }

    public static PageEntry fromPage(Page page) {
        if (page == null) {
            return null;
        }
        PageEntry entry = new PageEntry();
        entry.setId(page.getId());
        entry.setTitle(page.getTitle());
        entry.setAlias(page.getAlias());
        entry.setSliderHeader(page.getSliederHeader());
        entry.setSliderPromo(page.getSliderPromo());
        entry.setSliderAnnotation(page.getSliderAnnotation());
//        try {
//            entry.setContent(new String(Base64.getDecoder().decode(page.getContent())));
//        } catch (Exception e) {
            entry.setContent(page.getContent());
//        }

//        if (page.getContent() != null) {
//            entry.setContent(page
//                    .getContent()
//                    .replaceAll("<br/>", "\n")
//                    .replaceAll("&nbsp;&nbsp;&nbsp;", "\t")
//                    .replaceAll("&nbsp;", " "));
//        }

        entry.setImage(ImageEntry.fromImage(page.getImage()));
        entry.setSliderImage(ImageEntry.fromImage(page.getSliderImage()));
        entry.setSlider(page.isSlider());
        entry.setShowInMainMenu(page.getShowInMainMenu());
        entry.setShowInSideMenu(page.getShowInSideMenu());
        if (page.getParentPage() != null) {
            entry.setParentPageId(page.getParentPage().getId());
            entry.setParentPageTitle(page.getParentPage().getTitle());
        }
        return entry;
    }
}
