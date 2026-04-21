package com.ab.ms.payment.domain;

public interface ProductCatalog {
    Product get(String productId);
    boolean exists(String productId);
}
