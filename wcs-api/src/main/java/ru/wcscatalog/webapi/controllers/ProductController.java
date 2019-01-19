package ru.wcscatalog.webapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.wcscatalog.core.dto.ProductEntry;
import ru.wcscatalog.core.repository.ProductRepository;

import java.util.List;

@Controller
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository categoriesRepository) {
        this.productRepository = categoriesRepository;
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ProductEntry>> getPopularProducts() {
        List<ProductEntry> products = productRepository.getPopularProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/byAlias/{alias}")
    public ResponseEntity<ProductEntry> getProductByAlias(@PathVariable("alias") String alias) {
        ProductEntry product = productRepository.getProductByAlias(alias);
        return ResponseEntity.ok(product);
    }
}
