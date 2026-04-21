package com.ab.ms.order.exceptions;

import java.util.UUID;

public class MismatchedOrderIdSagaException extends NonRetryableSagaException {

    public MismatchedOrderIdSagaException(UUID eventOrderId, UUID orderID) {
        super("Saga event Order ID %s does not match provided Order ID %s"
            .formatted(eventOrderId, orderID));
    }
}
