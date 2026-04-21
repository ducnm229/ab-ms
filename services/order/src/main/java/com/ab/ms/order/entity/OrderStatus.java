package com.ab.ms.order.entity;

/**
 * Persisted states for orchestrated saga (Order as orchestrator and source of truth).
 */
public enum OrderStatus {
    CREATED,
    PENDING,
    INVENTORY_RESERVED,
    PAYMENT_COMPLETED,
    CANCELLING,
    CANCELLED,
    CONFIRMED
}
