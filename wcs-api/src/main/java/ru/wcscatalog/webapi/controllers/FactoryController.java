package ru.wcscatalog.webapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.wcscatalog.core.dto.FactoryEntry;
import ru.wcscatalog.core.repository.CategoriesRepository;
import ru.wcscatalog.core.repository.FactoryRepository;

import java.util.List;

@Controller
@RequestMapping("/brands")
public class FactoryController {
    private final FactoryRepository factoryRepository;

    public FactoryController(FactoryRepository factoryRepository) {
        this.factoryRepository = factoryRepository;
    }

    @GetMapping("/popular")
    public ResponseEntity<List<FactoryEntry>> getSideMenuItems() {
        List<FactoryEntry> factories = factoryRepository.getPopularBrands();
        return ResponseEntity.ok(factories);
    }
}
