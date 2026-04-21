package com.ab.ms.inventory.domain;

import java.util.Collection;

public interface ProductCatalog {
    Product get(String productId);
    boolean exists(String productId);
    Collection<Product> getAll();
}
