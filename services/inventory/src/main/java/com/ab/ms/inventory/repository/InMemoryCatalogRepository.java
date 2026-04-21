package com.ab.ms.inventory.repository;

import com.ab.ms.inventory.domain.Product;
import com.ab.ms.inventory.domain.ProductCatalog;
import com.ab.ms.inventory.dto.CatalogPage;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

@Repository
public class InMemoryCatalogRepository implements CatalogRepository {

    private final ProductCatalog productCatalog;

    public InMemoryCatalogRepository(ProductCatalog productCatalog) {
        this.productCatalog = productCatalog;
    }

    @Override
    public CatalogPage findByCategory(String category, int page, int size) {
        String normalizedCategory = normalizeCategory(category);

        List<Product> filteredProducts = productCatalog.getAll().stream()
                .filter(product -> normalizedCategory == null
                        || product.category().toLowerCase(Locale.ROOT).equals(normalizedCategory))
                .toList();

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, filteredProducts.size());
        List<Product> paginatedProducts = fromIndex >= filteredProducts.size()
                ? List.of()
                : filteredProducts.subList(fromIndex, toIndex);

        long totalElements = filteredProducts.size();
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);

        return new CatalogPage(paginatedProducts, page, size, totalElements, totalPages);
    }

    private static String normalizeCategory(String category) {
        if (!StringUtils.hasText(category)) {
            return null;
        }
        return category.trim().toLowerCase(Locale.ROOT);
    }
}
