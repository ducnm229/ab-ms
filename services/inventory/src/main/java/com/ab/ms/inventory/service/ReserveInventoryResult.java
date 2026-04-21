package com.ab.ms.inventory.service;

import com.ab.ms.inventory.messaging.InventoryItemMessage;

import java.util.List;

public record ReserveInventoryResult(
        boolean success,
        String reason,
        List<InventoryItemMessage> reservedItems,
        List<InventoryItemMessage> failedItems) {

    public static ReserveInventoryResult ok(List<InventoryItemMessage> reservedItems) {
        return new ReserveInventoryResult(true, null, reservedItems, List.of());
    }

    public static ReserveInventoryResult failure(String reason, List<InventoryItemMessage> failedItems) {
        return new ReserveInventoryResult(false, reason, List.of(), failedItems);
    }
}
