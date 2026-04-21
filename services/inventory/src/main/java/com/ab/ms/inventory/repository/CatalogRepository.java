package com.ab.ms.inventory.repository;

import com.ab.ms.inventory.dto.CatalogPage;

public interface CatalogRepository {
    CatalogPage findByCategory(String category, int page, int size);
}
