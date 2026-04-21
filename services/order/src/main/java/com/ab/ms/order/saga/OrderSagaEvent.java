package com.ab.ms.order.saga;

import com.ab.ms.order.messaging.InventoryItemMessage;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;
import java.util.UUID;

/**
 * Saga events - internal and inbound
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(OrderSagaEvent.OrderCreatedEvent.class),
        @JsonSubTypes.Type(OrderSagaEvent.InventoryReservedEvent.class),
        @JsonSubTypes.Type(OrderSagaEvent.InventoryFailedEvent.class),
        @JsonSubTypes.Type(OrderSagaEvent.PaymentSucceededEvent.class),
        @JsonSubTypes.Type(OrderSagaEvent.PaymentFailedEvent.class),
        @JsonSubTypes.Type(OrderSagaEvent.InventoryReleasedEvent.class),
        @JsonSubTypes.Type(OrderSagaEvent.OrderConfirmedEvent.class)
})
public sealed interface OrderSagaEvent permits
        OrderSagaEvent.OrderCreatedEvent,
        OrderSagaEvent.InventoryReservedEvent,
        OrderSagaEvent.InventoryFailedEvent,
        OrderSagaEvent.PaymentSucceededEvent,
        OrderSagaEvent.PaymentFailedEvent,
        OrderSagaEvent.InventoryReleasedEvent,
        OrderSagaEvent.OrderConfirmedEvent {

    UUID orderId();
    
    @JsonTypeName("OrderCreated")
    record OrderCreatedEvent(UUID orderId) implements OrderSagaEvent {}

    @JsonTypeName("InventoryReserved")
    record InventoryReservedEvent(UUID orderId, List<InventoryItemMessage> reservedItems) implements OrderSagaEvent {}

    @JsonTypeName("InventoryFailed")
    record InventoryFailedEvent(UUID orderId, String reason, List<InventoryItemMessage> failedItems) implements OrderSagaEvent {}

    @JsonTypeName("PaymentSucceeded")
    record PaymentSucceededEvent(UUID orderId) implements OrderSagaEvent {}

    @JsonTypeName("PaymentFailed")
    record PaymentFailedEvent(UUID orderId, String reason) implements OrderSagaEvent {}

    @JsonTypeName("InventoryReleased")
    record InventoryReleasedEvent(UUID orderId) implements OrderSagaEvent {}

    @JsonTypeName("OrderConfirmed")
    record OrderConfirmedEvent(UUID orderId) implements OrderSagaEvent {}
}
