package com.ab.ms.order.exceptions;

public class UnknownProductException extends RuntimeException {
    public UnknownProductException(String productId) {
        super("Unknown product ID: " + productId);
    }
}
