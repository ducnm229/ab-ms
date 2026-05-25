package com.ab.ms.product.redis;

import com.ab.ms.product.dto.ProductResponse;

public record ProductUpdatedEvent(ProductResponse productResponse) {
}
