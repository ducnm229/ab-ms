package com.ab.ms.product.dto;

import java.util.List;

public record CatalogPage(
        List<ProductResponse> products,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
