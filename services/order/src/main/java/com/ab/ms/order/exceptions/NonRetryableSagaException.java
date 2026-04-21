package com.ab.ms.order.exceptions;

public abstract class NonRetryableSagaException extends RuntimeException {
    public NonRetryableSagaException(String message) {
        super(message);
    }
}
