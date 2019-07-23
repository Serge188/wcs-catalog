package ru.wcscatalog.core.dto;

import ru.wcscatalog.core.model.PhotoGalleryItem;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryItemEntry {
    private Long id;
    private String title;
    private String alias;
    private ImageEntry mainImage;
    private List<ImageEntry> images = new ArrayList<>();

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

    public ImageEntry getMainImage() {
        return mainImage;
    }

    public void setMainImage(ImageEntry mainImage) {
        this.mainImage = mainImage;
    }

    public List<ImageEntry> getImages() {
        return images;
    }

    public void setImages(List<ImageEntry> images) {
        this.images = images;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public static PhotoGalleryItemEntry fromGalleryItem(PhotoGalleryItem item) {
        PhotoGalleryItemEntry entry = new PhotoGalleryItemEntry();
        entry.setId(item.getId());
        entry.setTitle(item.getTitle());
        entry.setAlias(item.getAlias());
        if (item.getImages().size() > 0) {
//            entry.setMainImage(ImageEntry.fromImage(item.getImages().get(0)));
            item.getImages().forEach(image -> {
                    entry.getImages().add(ImageEntry.fromImage(image));
            });
        }

        return entry;
    }
}
