package com.ab.ms.payment.exceptions;

import com.ab.ms.payment.saga.PaymentSagaCommand;

public class IllegalInboundSagaCommandException extends NonRetryableSagaException {

    public IllegalInboundSagaCommandException(PaymentSagaCommand command, String channel) {
        super("Illegal inbound saga command (%s) on %s channel"
            .formatted(command.getClass().getSimpleName(), channel));
    }
}
