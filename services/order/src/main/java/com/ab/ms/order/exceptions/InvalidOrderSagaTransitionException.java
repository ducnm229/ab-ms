package com.ab.ms.order.exceptions;

import com.ab.ms.order.entity.OrderStatus;
import com.ab.ms.order.saga.OrderSagaEvent;

public class InvalidOrderSagaTransitionException extends NonRetryableSagaException {

    public InvalidOrderSagaTransitionException(OrderStatus currentStatus, OrderSagaEvent event) {
        super("Invalid saga transition: status=%s, event=%s".formatted(currentStatus, event.getClass().getSimpleName()));
    }
}
