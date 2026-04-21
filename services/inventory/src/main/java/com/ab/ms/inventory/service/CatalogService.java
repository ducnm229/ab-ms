package com.ab.ms.inventory.service;

import com.ab.ms.inventory.dto.CatalogPage;
import com.ab.ms.inventory.repository.CatalogRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Locale;
import java.util.Objects;

@Service
public class CatalogService {

    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    private final CatalogRepository catalogRepository;
    private final RedisTemplate<String, CatalogPage> catalogRedisTemplate;

    public CatalogService(
            CatalogRepository catalogRepository,
            RedisTemplate<String, CatalogPage> catalogRedisTemplate) {
        this.catalogRepository = catalogRepository;
        this.catalogRedisTemplate = catalogRedisTemplate;
    }

    public CatalogPage getCatalog(String category, int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page must be >= 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be > 0");
        }

        String cacheKey = buildCacheKey(category, page, size);
        CatalogPage cachedPage = catalogRedisTemplate.opsForValue().get(Objects.requireNonNull(cacheKey));
        if (cachedPage != null) {
            return cachedPage;
        }

        CatalogPage pageResult = catalogRepository.findByCategory(category, page, size);
        if (pageResult == null) {
            pageResult = new CatalogPage(java.util.List.of(), page, size, 0, 0);
        }
        catalogRedisTemplate.opsForValue().set(
                Objects.requireNonNull(cacheKey),
                pageResult,
                Objects.requireNonNull(CACHE_TTL));
        return pageResult;
    }

    private static String buildCacheKey(String category, int page, int size) {
        String normalizedCategory = StringUtils.hasText(category)
                ? category.trim().toLowerCase(Locale.ROOT)
                : "all";
        return "catalog:%s:%d:%d".formatted(normalizedCategory, page, size);
    }
}
