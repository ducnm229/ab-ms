package com.ab.ms.order.saga;

import com.ab.ms.order.messaging.InventoryItemMessage;
import com.ab.ms.order.entity.Order;
import com.ab.ms.order.entity.OrderStatus;
import com.ab.ms.order.exceptions.InvalidOrderSagaTransitionException;

import java.util.List;
import java.util.Optional;

/**
 * State transition handling
 */
public final class SagaTransitions {

    private SagaTransitions() {
    }

    public static SagaTransitionResult resolve(Order order, OrderSagaEvent event) {
        return switch (order.getStatus()) {
            case CREATED -> switch (event) {
                case OrderSagaEvent.OrderCreatedEvent ignored -> SagaTransitionResult.apply(new SagaStep(
                        OrderStatus.PENDING,
                        Optional.of(new OrderSagaCommand.ReserveInventory(order.getId(), mapItems(order))))
                );
                default -> throw invalid(order.getStatus(), event);
            };
            case PENDING -> switch (event) {
                case OrderSagaEvent.InventoryReservedEvent ignored -> SagaTransitionResult.apply(new SagaStep(
                        OrderStatus.INVENTORY_RESERVED,
                        Optional.of(new OrderSagaCommand.ProcessPayment(
                                order.getId(),
                                order.getCustomerId(),
                                order.getTotalAmount(),
                                "USD")))
                );
                case OrderSagaEvent.InventoryFailedEvent ignored ->
                        SagaTransitionResult.apply(new SagaStep(OrderStatus.CANCELLED, Optional.empty()));
                default -> throw invalid(order.getStatus(), event);
            };
            case INVENTORY_RESERVED -> switch (event) {
                case OrderSagaEvent.InventoryReservedEvent ignored -> SagaTransitionResult.noOpDuplicate();
                case OrderSagaEvent.PaymentSucceededEvent ignored -> SagaTransitionResult.apply(new SagaStep(
                        OrderStatus.PAYMENT_COMPLETED,
                        Optional.of(new OrderSagaCommand.ConfirmOrder(order.getId())))
                );
                case OrderSagaEvent.PaymentFailedEvent ignored -> SagaTransitionResult.apply(new SagaStep(
                        OrderStatus.CANCELLING,
                        Optional.of(new OrderSagaCommand.ReleaseInventory(order.getId())))
                );
                default -> throw invalid(order.getStatus(), event);
            };
            case CANCELLING -> switch (event) {
                case OrderSagaEvent.InventoryReleasedEvent ignored ->
                        SagaTransitionResult.apply(new SagaStep(OrderStatus.CANCELLED, Optional.empty()));
                case OrderSagaEvent.PaymentFailedEvent ignored -> SagaTransitionResult.noOpDuplicate();
                default -> throw invalid(order.getStatus(), event);
            };
            case PAYMENT_COMPLETED -> switch (event) {
                case OrderSagaEvent.InventoryReservedEvent ignored -> SagaTransitionResult.noOpDuplicate();
                case OrderSagaEvent.PaymentSucceededEvent ignored -> SagaTransitionResult.noOpDuplicate();
                case OrderSagaEvent.OrderConfirmedEvent ignored ->
                        SagaTransitionResult.apply(new SagaStep(OrderStatus.CONFIRMED, Optional.empty()));
                default -> throw invalid(order.getStatus(), event);
            };
            case CANCELLED -> switch (event) {
                case OrderSagaEvent.InventoryFailedEvent ignored -> SagaTransitionResult.noOpDuplicate();
                case OrderSagaEvent.PaymentFailedEvent ignored -> SagaTransitionResult.noOpDuplicate();
                case OrderSagaEvent.InventoryReleasedEvent ignored -> SagaTransitionResult.noOpDuplicate();
                default -> throw invalid(order.getStatus(), event);
            };
            case CONFIRMED -> switch (event) {
                case OrderSagaEvent.InventoryReservedEvent ignored -> SagaTransitionResult.noOpDuplicate();
                case OrderSagaEvent.PaymentSucceededEvent ignored -> SagaTransitionResult.noOpDuplicate();
                case OrderSagaEvent.OrderConfirmedEvent ignored -> SagaTransitionResult.noOpDuplicate();
                default -> throw invalid(order.getStatus(), event);
            };
        };
    }

    private static List<InventoryItemMessage> mapItems(Order order) {
        return order.getItems().stream()
                .map(i -> new InventoryItemMessage(i.getProductId(), i.getQuantity()))
                .toList();
    }

    private static InvalidOrderSagaTransitionException invalid(OrderStatus current, OrderSagaEvent event) {
        return new InvalidOrderSagaTransitionException(current, event);
    }
}
