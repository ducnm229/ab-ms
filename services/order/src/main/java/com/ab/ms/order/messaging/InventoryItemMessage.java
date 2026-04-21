package com.ab.ms.order.messaging;

/**
 * Saga message line item: product identity and quantity (JSON contract with Inventory service).
 */
public record InventoryItemMessage(String productId, int quantity) {}
