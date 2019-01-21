package ru.wcscatalog.webapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.wcscatalog.core.dto.CategoryEntry;
import ru.wcscatalog.core.model.Category;
import ru.wcscatalog.core.repository.CategoriesRepository;

import java.util.ArrayList;
import java.util.List;

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
}
