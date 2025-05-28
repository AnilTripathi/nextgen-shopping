package com.shopping.product.controller;

import com.shopping.product.model.Product;
import com.shopping.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "Endpoints for managing products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all active products")
    public ResponseEntity<Page<Product>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/vendor/{vendorId}")
    @Operation(summary = "Get products by vendor")
    public ResponseEntity<Page<Product>> getProductsByVendor(
            @PathVariable Long vendorId, Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByVendor(vendorId, pageable));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category")
    public ResponseEntity<Page<Product>> getProductsByCategory(
            @PathVariable String category, Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByCategory(category, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam String query, Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(query, pageable));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all product categories")
    public ResponseEntity<List<String>> getAllCategories() {
        return ResponseEntity.ok(productService.getAllCategories());
    }

    @PostMapping
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Create a new product")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id, @Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Delete a product")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/inventory")
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Update product inventory")
    public ResponseEntity<Product> updateInventory(
            @PathVariable Long id, @RequestParam Integer quantity) {
        return ResponseEntity.ok(productService.updateInventory(id, quantity));
    }
} 