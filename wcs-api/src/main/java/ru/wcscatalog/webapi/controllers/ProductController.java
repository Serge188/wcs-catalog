package ru.wcscatalog.webapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.wcscatalog.core.dto.ProductEntry;
import ru.wcscatalog.core.dto.ProductInput;
import ru.wcscatalog.core.dto.SaleOfferInput;
import ru.wcscatalog.core.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
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

    @GetMapping("/byCategory/{categoryId}")
    public ResponseEntity<List<ProductEntry>> getProductsByCategory(@PathVariable("categoryId") Long categoryId) {
        List<ProductEntry> products = productRepository.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/byCategoryForOneLevel/{categoryId}")
    public ResponseEntity<List<ProductEntry>> getProductsByCategoryForOneLevel(@PathVariable("categoryId") Long categoryId) {
        List<ProductEntry> products = productRepository.getProductsByCategoryForOneLevel(categoryId);
        return ResponseEntity.ok(products);
    }

    @PostMapping()
    public ResponseEntity<?> createNewProduct(@RequestBody ProductInput input) {
        try {
            productRepository.createOrUpdateProduct(input);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.of(Optional.of("error.creating.new.product"));
        }
    }

    @PutMapping()
    public ResponseEntity<?> updateProduct(@RequestBody ProductInput input) {
        try {
            productRepository.createOrUpdateProduct(input);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.of(Optional.of("error.updating.new.product"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeProduct(@PathVariable("id") Long productId) {
        productRepository.removeProduct(productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/saleOffer")
    public ResponseEntity<?> createSaleOffer(@RequestBody SaleOfferInput input) {
        try {
            productRepository.createOrUpdateSaleOffer(input, null);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.of(Optional.of("error.creating.new.sale.offer"));
        }
    }

    @PutMapping("/saleOffer")
    public ResponseEntity<?> updateSaleOffer(@RequestBody SaleOfferInput input) {
        try {
            productRepository.createOrUpdateSaleOffer(input, null);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.of(Optional.of("error.updating.sale.offer"));
        }
    }
    @DeleteMapping("/saleOffer/{id}")
    public ResponseEntity<?> removeSaleOffer(@PathVariable("id") Long saleOfferId) {
        productRepository.removeSaleOfferFromProduct(saleOfferId);
        return ResponseEntity.ok().build();
    }
}
