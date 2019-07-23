package ru.wcscatalog.webapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.wcscatalog.core.dto.PhotoGalleryItemEntry;
import ru.wcscatalog.core.dto.PhotoGalleryItemInput;
import ru.wcscatalog.core.repository.PhotoGalleryItemsRepository;

import java.util.List;

@Controller
@RequestMapping("/photoGalleryItems")
public class PhotoGalleryItemsController {

    private final PhotoGalleryItemsRepository photoGalleryItemsRepository;

    public PhotoGalleryItemsController(PhotoGalleryItemsRepository photoGalleryItemsRepository) {
        this.photoGalleryItemsRepository = photoGalleryItemsRepository;
    }

    @GetMapping
    public ResponseEntity<List<PhotoGalleryItemEntry>> getAll() {
        List<PhotoGalleryItemEntry> entries = photoGalleryItemsRepository.getAll();
        return ResponseEntity.ok(entries);
    }

    @PostMapping
    public ResponseEntity<?> createOrUpdate(@RequestBody PhotoGalleryItemInput input) throws Exception {
        photoGalleryItemsRepository.createOrUpdateGalleryItem(input);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePhotoGalleryItem(@PathVariable("id") Long itemId) {
        photoGalleryItemsRepository.removePhotoGalleryItem(itemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/image/{imageId}")
    public ResponseEntity<?> deleteImageFromPhotoGalleryItem(@PathVariable("imageId") Long imageId) {
        photoGalleryItemsRepository.removeImageFromGalleryItem(imageId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<?> addImageToPhotoGalleryItem(@PathVariable("id") Long itemId, @RequestBody String imageInput) throws Exception {
        photoGalleryItemsRepository.addImageToGalleryItem(itemId, imageInput);
        return ResponseEntity.ok().build();
    }
}
