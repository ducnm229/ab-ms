package com.ab.ms.order.domain;

import java.math.BigDecimal;

public record Product(String productId, String name, BigDecimal price) {
} 
