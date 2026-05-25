package com.ab.ms.product.redis;

import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Locale;

public final class ProductCacheKeys {

    public static final Duration PRODUCT_CACHE_TTL = Duration.ofHours(1);
    public static final Duration CATALOG_CACHE_TTL = Duration.ofMinutes(5);
    private static final String PRODUCT_PREFIX = "product:";

    private ProductCacheKeys() {}

    public static String productKey(String id) {
        return PRODUCT_PREFIX + id;
    }

    public static String catalogKey(String category, int page, int size) {
        String normalizedCategory = StringUtils.hasText(category)
                ? category.trim().toLowerCase(Locale.ROOT)
                : "all";
        return "catalog:%s:%d:%d".formatted(normalizedCategory, page, size);
    }
}
