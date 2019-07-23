package ru.wcscatalog.webapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import ru.wcscatalog.core.dto.FactoryEntry;
import ru.wcscatalog.core.dto.FactoryInput;
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

    @GetMapping()
    public ResponseEntity<List<FactoryEntry>> getAllFactories() {
        List<FactoryEntry> factories = factoryRepository.getAllFactories();
        return ResponseEntity.ok(factories);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<FactoryEntry>> getPopularBrands() {
        List<FactoryEntry> factories = factoryRepository.getPopularBrands();
        return ResponseEntity.ok(factories);
    }

    @PostMapping()
    public ResponseEntity<?> createFactory(@RequestBody FactoryInput input) throws Exception {
        factoryRepository.createFactory(input);
        return ResponseEntity.ok().build();
    }

    @PutMapping()
    public ResponseEntity<?> updateFactory(@RequestBody FactoryInput input) throws Exception {
        factoryRepository.updateFactory(input);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFactory(@PathVariable("id") Long id) {
        factoryRepository.removeFactory(id);
        return ResponseEntity.ok().build();
    }
}
