package ru.wcscatalog.webapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.wcscatalog.core.dto.CategoryEntry;
import ru.wcscatalog.core.model.Category;
import ru.wcscatalog.core.repository.CategoriesRepository;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/categories")
public class MenuController {
    private final CategoriesRepository categoriesRepository;

    public MenuController(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    @GetMapping("/sideMenuCategories")
    public ResponseEntity<List<CategoryEntry>> getSideMenuItems() {
        List<Category> categories = categoriesRepository.getCategories();
        List<CategoryEntry> categoryEntries = new ArrayList<>();
        for (Category category: categories) {
            CategoryEntry entry = new CategoryEntry();
            entry.setId(category.getId());
            entry.setLink(category.getLink());
            entry.setTitle(category.getTitle());
            if (category.getParentCategory() != null) {
                entry.setParentCategoryId(category.getParentCategory().getId());
            }
            categoryEntries.add(entry);
        }
        return ResponseEntity.ok(categoryEntries);
    }
}
