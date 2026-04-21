package com.ab.ms.inventory.messaging;

/**
 * Saga message line item — same JSON shape as {@code com.ab.ms.order.messaging.InventoryItemMessage}.
 */
public record InventoryItemMessage(String productId, int quantity) {}
