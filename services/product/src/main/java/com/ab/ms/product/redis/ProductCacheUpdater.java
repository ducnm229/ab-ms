package com.ab.ms.product.redis;

import com.ab.ms.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCacheUpdater {
    private final RedisTemplate<String, ProductResponse> redisTemplate;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(ProductUpdatedEvent event) {

        ProductResponse productResponse = event.productResponse();

        redisTemplate.opsForValue().set(
                ProductCacheKeys.productKey(productResponse.id()),
                productResponse,
                ProductCacheKeys.PRODUCT_CACHE_TTL
        );
        log.info("Cache updated for product id={}", productResponse.id());
    }
}
