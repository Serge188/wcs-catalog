package ru.wcscatalog.webapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.wcscatalog.core.dto.CategoryEntry;
import ru.wcscatalog.core.dto.CategoryInput;
import ru.wcscatalog.core.model.Category;
import ru.wcscatalog.core.repository.CategoriesRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoriesRepository categoriesRepository;

    public CategoryController(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    @GetMapping("/sideMenuCategories")
    public ResponseEntity<List<CategoryEntry>> getSideMenuItems() {
        List<CategoryEntry> categories = categoriesRepository.getCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/byAlias/{alias}")
    public ResponseEntity<CategoryEntry> getCategoryByAlias(@PathVariable("alias") String alias) {
        CategoryEntry category = categoriesRepository.getCategoryByAlias(alias);
        return ResponseEntity.ok(category);
    }

    @PutMapping()
    public ResponseEntity<?> updateCategory(@RequestBody CategoryInput input) {
        try {
            categoriesRepository.updateCategory(input);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.of(Optional.of("error.image.was.not.uploaded"));
        }

    }

    @PostMapping("/new")
    public ResponseEntity<?> createCategory(@RequestBody CategoryInput input) {
        try {
            categoriesRepository.createCategory(input);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.of(Optional.of("error.image.was.not.uploaded"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> createCategory(@PathVariable("id") Long categoryId) {
        try {
            categoriesRepository.removeCategory(categoryId);
            return ResponseEntity.ok().build();
        } catch (Exception e ) {
            return ResponseEntity.badRequest().body("error.occurred.while.removing.category");
        }

    }
}
