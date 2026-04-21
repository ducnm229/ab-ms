package com.ab.ms.inventory.saga;

import com.ab.ms.inventory.messaging.InventoryItemMessage;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;
import java.util.UUID;

/**
 * Inbound saga commands (dispatched from Order)
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(InventorySagaCommand.ReserveInventory.class),
        @JsonSubTypes.Type(InventorySagaCommand.ReleaseInventory.class)
})
public sealed interface InventorySagaCommand permits InventorySagaCommand.ReserveInventory,
        InventorySagaCommand.ReleaseInventory {

    UUID orderId();

    @JsonTypeName("ReserveInventory")
    record ReserveInventory(UUID orderId, List<InventoryItemMessage> items) implements InventorySagaCommand {}

    @JsonTypeName("ReleaseInventory")
    record ReleaseInventory(UUID orderId) implements InventorySagaCommand {}
}
