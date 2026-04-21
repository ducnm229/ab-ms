package com.ab.ms.order.saga;

import com.ab.ms.order.messaging.InventoryItemMessage;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Outbound saga commands (sent to Inventory and Payment)
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(OrderSagaCommand.ReserveInventory.class),
        @JsonSubTypes.Type(OrderSagaCommand.ProcessPayment.class),
        @JsonSubTypes.Type(OrderSagaCommand.ReleaseInventory.class),
        @JsonSubTypes.Type(OrderSagaCommand.ConfirmOrder.class)
})
public sealed interface OrderSagaCommand permits
        OrderSagaCommand.ReserveInventory,
        OrderSagaCommand.ProcessPayment,
        OrderSagaCommand.ReleaseInventory,
        OrderSagaCommand.ConfirmOrder {

    UUID orderId();

    @JsonTypeName("ReserveInventory")
    record ReserveInventory(UUID orderId, List<InventoryItemMessage> items) implements OrderSagaCommand {}

    @JsonTypeName("ProcessPayment")
    record ProcessPayment(UUID orderId, Long customerId, BigDecimal amount, String currency) implements OrderSagaCommand {}

    @JsonTypeName("ReleaseInventory")
    record ReleaseInventory(UUID orderId) implements OrderSagaCommand {}

    @JsonTypeName("ConfirmOrder")
    record ConfirmOrder(UUID orderId) implements OrderSagaCommand {}
}
