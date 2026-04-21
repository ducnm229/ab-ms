package com.ab.ms.order.exceptions;

public class InvalidCustomerException extends RuntimeException {

    public InvalidCustomerException(Long customerId) {
        super("Customer validation failed for customerId: " + customerId);
    }
}
