package com.ab.ms.order.domain;

public interface ProductCatalog {
    Product get(String productId);
    boolean exists(String productId);
}
