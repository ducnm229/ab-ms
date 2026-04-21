package com.ab.ms.payment.saga;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Inbound saga commands (dispatched from Order)
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(PaymentSagaCommand.ProcessPayment.class)
})
public sealed interface PaymentSagaCommand permits PaymentSagaCommand.ProcessPayment {

    UUID orderId();

    @JsonTypeName("ProcessPayment")
    record ProcessPayment(
            UUID orderId,
            Long customerId,
            BigDecimal amount,
            String currency) implements PaymentSagaCommand {}
}
