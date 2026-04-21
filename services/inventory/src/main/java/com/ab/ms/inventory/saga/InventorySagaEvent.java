package com.ab.ms.inventory.saga;

import com.ab.ms.inventory.messaging.InventoryItemMessage;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;
import java.util.UUID;

/**
 * Outbound saga events (sent to the Order orchestrator)
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(InventorySagaEvent.InventoryReservedEvent.class),
        @JsonSubTypes.Type(InventorySagaEvent.InventoryFailedEvent.class),
        @JsonSubTypes.Type(InventorySagaEvent.InventoryReleasedEvent.class)
})
public sealed interface InventorySagaEvent permits InventorySagaEvent.InventoryReservedEvent,
        InventorySagaEvent.InventoryFailedEvent,
        InventorySagaEvent.InventoryReleasedEvent {

    UUID orderId();

    @JsonTypeName("InventoryReserved")
    record InventoryReservedEvent(UUID orderId, List<InventoryItemMessage> reservedItems) implements InventorySagaEvent {}

    @JsonTypeName("InventoryFailed")
    record InventoryFailedEvent(UUID orderId, String reason, List<InventoryItemMessage> failedItems)
            implements InventorySagaEvent {}

    @JsonTypeName("InventoryReleased")
    record InventoryReleasedEvent(UUID orderId) implements InventorySagaEvent {}
}
