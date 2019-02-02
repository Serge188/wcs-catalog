package ru.wcscatalog.core.dto;

import ru.wcscatalog.core.model.Image;

public class ImageEntry {
    private long id;
    private String originalImageLink;
    private String baseImageLink;
    private String cardImageLink;
    private String galleryImageLink;
    private String previewImageLink;
    private String optionImageLink;
    private String categoryImageLink;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOriginalImageLink() {
        return originalImageLink;
    }

    public void setOriginalImageLink(String originalImageLink) {
        this.originalImageLink = originalImageLink;
    }

    public String getBaseImageLink() {
        return baseImageLink;
    }

    public void setBaseImageLink(String baseImageLink) {
        this.baseImageLink = baseImageLink;
    }

    public String getCardImageLink() {
        return cardImageLink;
    }

    public void setCardImageLink(String cardImageLink) {
        this.cardImageLink = cardImageLink;
    }

    public String getGalleryImageLink() {
        return galleryImageLink;
    }

    public void setGalleryImageLink(String galleryImageLink) {
        this.galleryImageLink = galleryImageLink;
    }

    public String getPreviewImageLink() {
        return previewImageLink;
    }

    public void setPreviewImageLink(String previewImageLink) {
        this.previewImageLink = previewImageLink;
    }

    public String getOptionImageLink() {
        return optionImageLink;
    }

    public void setOptionImageLink(String optionImageLink) {
        this.optionImageLink = optionImageLink;
    }

    public String getCategoryImageLink() {
        return categoryImageLink;
    }

    public void setCategoryImageLink(String categoryImageLink) {
        this.categoryImageLink = categoryImageLink;
    }

    public static ImageEntry fromImage(Image image) {
        if (image != null) {
            ImageEntry entry = new ImageEntry();
            entry.setId(image.getId());
            entry.setOriginalImageLink(image.getOriginalImageLink());
            entry.setBaseImageLink(image.getBaseImageLink());
            entry.setCardImageLink(image.getCardImageLink());
            entry.setGalleryImageLink(image.getGalleryImageLink());
            entry.setPreviewImageLink(image.getPreviewImageLink());
            entry.setOptionImageLink(image.getOptionImageLink());
            entry.setCategoryImageLink(image.getCategoryImageLink());
            return entry;
        }
        return null;
    }
}
