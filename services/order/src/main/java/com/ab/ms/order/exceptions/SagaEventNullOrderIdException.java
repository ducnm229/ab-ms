package com.ab.ms.order.exceptions;

import com.ab.ms.order.saga.OrderSagaEvent;

public class SagaEventNullOrderIdException extends NonRetryableSagaException {

    public SagaEventNullOrderIdException(OrderSagaEvent event) {
        super("Event " + event.getClass().getSimpleName() + " missing Order ID");
    }
}
