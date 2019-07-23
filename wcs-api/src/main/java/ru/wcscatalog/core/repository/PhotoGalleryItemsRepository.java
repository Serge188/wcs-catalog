package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.wcscatalog.core.dto.PhotoGalleryItemEntry;
import ru.wcscatalog.core.dto.PhotoGalleryItemInput;
import ru.wcscatalog.core.model.Image;
import ru.wcscatalog.core.model.PhotoGalleryItem;
import ru.wcscatalog.core.utils.AliasChecker;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class PhotoGalleryItemsRepository {

    private final Dao dao;
    private final ImageRepository imageRepository;
    private final AliasChecker aliasChecker;

    public PhotoGalleryItemsRepository(Dao dao, ImageRepository imageRepository, AliasChecker aliasChecker) {
        this.dao = dao;
        this.imageRepository = imageRepository;
        this.aliasChecker = aliasChecker;
    }

    public List<PhotoGalleryItemEntry> getAll() {
        return dao
                .getAll(PhotoGalleryItem.class)
                .stream()
                .map(PhotoGalleryItemEntry::fromGalleryItem)
                .collect(Collectors.toList());
    }

    public void createOrUpdateGalleryItem(PhotoGalleryItemInput input) throws Exception {
        PhotoGalleryItem photoGalleryItem;
        if (input.getId() != null) {
          photoGalleryItem = dao.byId(input.getId(), PhotoGalleryItem.class);
        } else {
            photoGalleryItem = new PhotoGalleryItem();
            photoGalleryItem.setAlias(aliasChecker.findUniqueAliasForEntity(photoGalleryItem.getClass(), input.getTitle()));
            dao.add(photoGalleryItem);
        }
        photoGalleryItem.setTitle(input.getTitle());
//        String mainImageData = ((String) input.getMainImage());
//        photoGalleryItem.getImages().add(imageRepository.createImageForGalleryItem(mainImageData, input.getTitle(), true));
//        for (Object o : input.getImages()) {
//            String imageData = ((String) o);
//            photoGalleryItem.getImages().add(imageRepository.createImageForGalleryItem(imageData, input.getTitle(), false));
//        }
    }

    public void addImageToGalleryItem(Long itemId, String imageInput) throws Exception {
        PhotoGalleryItem photoGalleryItem = dao.byId(itemId, PhotoGalleryItem.class);
        Image image = imageRepository.createImageForGalleryItem(imageInput, photoGalleryItem.getTitle(), false);
        image.setGalleryItem(photoGalleryItem);
        photoGalleryItem.getImages().add(image);
    }

    public void removeImageFromGalleryItem(Long imageId) {
        imageRepository.removePhotoGalleryImage(imageId);
    }

    public void removePhotoGalleryItem(Long itemId) {
        PhotoGalleryItem item = dao.byId(itemId, PhotoGalleryItem.class);
        item.getImages().forEach(i -> removeImageFromGalleryItem(i.getId()));
        dao.remove(item);
    }
}
