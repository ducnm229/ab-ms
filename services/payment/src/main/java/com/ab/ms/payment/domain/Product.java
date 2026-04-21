package com.ab.ms.payment.domain;

import java.math.BigDecimal;

public record Product(String productId, String name, BigDecimal price) {
} 
