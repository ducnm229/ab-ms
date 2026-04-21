package com.ab.ms.inventory.domain;

import java.math.BigDecimal;

public record Product(String productId, String name, BigDecimal price, String category) {
} 
