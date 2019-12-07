package ru.wcscatalog.webapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.wcscatalog.core.dto.*;
import ru.wcscatalog.core.service.ProductService;
import ru.wcscatalog.core.repository.ImageRepository;
import ru.wcscatalog.core.repository.ProductRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final ProductService productService;

    public ProductController(@NonNull  ProductRepository categoriesRepository,
                             @NonNull ImageRepository imageRepository,
                             @NonNull ProductService productService) {
        this.productRepository = categoriesRepository;
        this.imageRepository = imageRepository;
        this.productService = productService;
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

    @PostMapping("/byCategory/{categoryId}")
    public ResponseEntity<List<ProductEntry>> getProductsByCategory(@PathVariable("categoryId") Long categoryId,
                                                                    @Nullable @RequestBody CategoryFilter categoryFilter) {
        List<ProductEntry> products = productRepository.getProductsByCategory(categoryId, categoryFilter);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/byCategoryForOneLevel/{categoryId}")
    public ResponseEntity<List<ProductEntry>> getProductsByCategoryForOneLevel(@PathVariable("categoryId") Long categoryId) {
        List<ProductEntry> products = productRepository.getProductsByCategoryForOneLevel(categoryId);
        return ResponseEntity.ok(products);
    }

    @PostMapping()
    public ResponseEntity<?> createNewProduct(@RequestBody ProductInput input) throws Exception {
        productRepository.createOrUpdateProduct(input);
        return ResponseEntity.ok().build();
    }

    @PutMapping()
    public ResponseEntity<?> updateProduct(@RequestBody ProductInput input) throws Exception {
        productRepository.createOrUpdateProduct(input);
        return ResponseEntity.ok().build();
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

    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<?> removeImageFromProduct(@PathVariable("imageId") Long imageId) {
        imageRepository.removeImageFromProduct(imageId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadProductsFromFile(@RequestBody String fileData) throws IOException {
        productService.createProductsFromFile(fileData);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/simplified")
    public ResponseEntity<?> getSimplifiedProducts(@RequestBody List<Long> productIds) {
        List<ProductSimplifiedEntry> entries = productRepository.getSimplifiedProducts(productIds);
        return ResponseEntity.ok(entries);
    }
}
