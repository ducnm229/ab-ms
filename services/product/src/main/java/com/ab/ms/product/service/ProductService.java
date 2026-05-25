package com.ab.ms.product.service;

import com.ab.ms.product.dto.CatalogPage;
import com.ab.ms.product.dto.ProductResponse;
import com.ab.ms.product.dto.UpdateProductRequest;
import com.ab.ms.product.entity.Product;
import com.ab.ms.product.exceptions.ResourceNotFoundException;
import com.ab.ms.product.redis.LockToken;
import com.ab.ms.product.redis.ProductCacheKeys;
import com.ab.ms.product.redis.ProductUpdatedEvent;
import com.ab.ms.product.redis.RedisLockService;
import com.ab.ms.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, ProductResponse> productRedisTemplate;
    private final RedisTemplate<String, CatalogPage> catalogRedisTemplate;
    private final RedisLockService redisLockService;
    private final ApplicationEventPublisher publisher;

    public ProductResponse getById(String id) {
        String cacheKey = ProductCacheKeys.productKey(id);
        /*
         * The following code is a cache-aside implementation with
         * mutex-based cache stampede prevention
         */
        while (true) {
            // 1. Try cache first
            ProductResponse cached = productRedisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("Cache hit for product id={}", id);
                return cached;
            }
            log.info("Cache miss for product id={}", id);

            // 2. Try acquiring regeneration lock
            Optional<LockToken> tokenOpt = redisLockService.tryLock(id);
            if (tokenOpt.isPresent()) {
                try {
                    log.info("Lock acquired for product id={}", id);
                    // 3. Double-check cache after lock acquisition
                    cached = productRedisTemplate.opsForValue().get(cacheKey);
                    if (cached != null) {
                        log.info("Cache already populated for product id={}. Returning.", id);
                        return cached;
                    }

                    // 4. Load from DB
                    log.info("Loading product id={} from database...", id);
                    simulateDbLatency();

                    Product product = productRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id) );

                    ProductResponse response = toResponse(product);

                    // Optional TTL jitter
                    Duration ttl = ProductCacheKeys.PRODUCT_CACHE_TTL
                            .plusSeconds(ThreadLocalRandom.current().nextInt(30));

                    // 5. Populate cache
                    productRedisTemplate.opsForValue().set(cacheKey, response, ttl);

                    return response;
                } finally {
                    // 6. Safe unlock
                    redisLockService.unlock(tokenOpt.get());
                }
            }

            // 7. Cache being regenerated elsewhere
            log.info("Waiting for cache population for product id={}", id);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for cache population", e);
            }
        }
    }

    @Transactional
    public ProductResponse update(String id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setName(request.name());
        product.setCategory(request.category());
        product.setPrice(request.price());
        Product saved = productRepository.save(product);
        ProductResponse productResponse = toResponse(saved);

        // write-through; update cache after commit
        publisher.publishEvent(new ProductUpdatedEvent(productResponse));

        return productResponse;
    }

    public CatalogPage getCatalog(String category, int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page must be >= 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be > 0");
        }

        String cacheKey = ProductCacheKeys.catalogKey(category, page, size);
        CatalogPage cached = catalogRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("Cache hit for catalog key={}", cacheKey);
            return cached;
        }

        log.info("Cache miss for catalog category key={}. Loading from database...", cacheKey);
        simulateDbLatency();

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> resultPage = StringUtils.hasText(category)
                ? productRepository.findByCategoryIgnoreCase(category.trim(), pageable)
                : productRepository.findAll(pageable);

        List<ProductResponse> products = resultPage.getContent().stream()
                .map(ProductService::toResponse)
                .toList();

        CatalogPage catalogPage = new CatalogPage(
                products,
                page,
                size,
                resultPage.getTotalElements(),
                resultPage.getTotalPages());

        catalogRedisTemplate.opsForValue().set(cacheKey, catalogPage, ProductCacheKeys.CATALOG_CACHE_TTL);
        return catalogPage;
    }

    private static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice());
    }

    private static void simulateDbLatency() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Thread interrupted while simulating database latency");
        }
    }
}
