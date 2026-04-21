package com.ab.ms.payment.exceptions;

public abstract class NonRetryableSagaException extends RuntimeException {
    public NonRetryableSagaException(String message) {
        super(message);
    }
}
