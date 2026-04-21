package com.ab.ms.inventory.dto;

import com.ab.ms.inventory.domain.Product;

import java.util.List;

public record CatalogPage(
        List<Product> products,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
