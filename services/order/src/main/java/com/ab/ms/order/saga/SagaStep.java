package com.ab.ms.order.saga;

import com.ab.ms.order.entity.OrderStatus;

import java.util.Optional;

/**
 * One orchestration step
 */
public record SagaStep(OrderStatus nextStatus, Optional<OrderSagaCommand> nextCommand) {
}
