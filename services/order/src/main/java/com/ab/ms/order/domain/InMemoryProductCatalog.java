package com.ab.ms.order.domain;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class InMemoryProductCatalog implements ProductCatalog {

    private final Map<String, Product> products = Map.of(
        "K100", new Product("K100", "Electric Kettle", BigDecimal.valueOf(30)),
        "K200", new Product("K200", "Toaster", BigDecimal.valueOf(25)),
        "K300", new Product("K300", "Blender", BigDecimal.valueOf(45)),
        "K400", new Product("K400", "Microwave Oven", BigDecimal.valueOf(120)),
        "K500", new Product("K500", "Coffee Maker", BigDecimal.valueOf(80))
    );

    public Product get(String id) {
        return products.get(id);
    }

    public boolean exists(String id) {
        return products.containsKey(id);
    }
}