package com.ab.ms.product.controller;

import com.ab.ms.product.dto.CatalogPage;
import com.ab.ms.product.dto.ProductResponse;
import com.ab.ms.product.dto.UpdateProductRequest;
import com.ab.ms.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products/{id}")
    public ProductResponse getProduct(@PathVariable String id) {
        return productService.getById(id);
    }

    @PutMapping("/products/{id}")
    public ProductResponse updateProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request) {
        return productService.update(id, request);
    }

    @GetMapping("/catalog")
    public CatalogPage getCatalog(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getCatalog(category, page, size);
    }
}
