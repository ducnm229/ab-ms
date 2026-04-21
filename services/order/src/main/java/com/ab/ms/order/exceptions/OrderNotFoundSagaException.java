package com.ab.ms.order.exceptions;

import java.util.UUID;

public class OrderNotFoundSagaException extends NonRetryableSagaException {

    public OrderNotFoundSagaException(UUID orderId) {
        super("Order not found for saga event: id=" + orderId);
    }
}
