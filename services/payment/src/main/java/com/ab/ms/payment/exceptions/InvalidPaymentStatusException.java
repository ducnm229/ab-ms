package com.ab.ms.payment.exceptions;

public class InvalidPaymentStatusException extends NonRetryableSagaException {

    public InvalidPaymentStatusException(String status) {
        super("Invalid payment status: %s".formatted(status));
    }
}
