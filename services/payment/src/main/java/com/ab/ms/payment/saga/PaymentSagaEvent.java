package com.ab.ms.payment.saga;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.UUID;

/**
 * Outbound saga events (sent to Order)
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(PaymentSagaEvent.PaymentSucceededEvent.class),
        @JsonSubTypes.Type(PaymentSagaEvent.PaymentFailedEvent.class)
})
public sealed interface PaymentSagaEvent permits PaymentSagaEvent.PaymentSucceededEvent,
        PaymentSagaEvent.PaymentFailedEvent {

    UUID orderId();

    @JsonTypeName("PaymentSucceeded")
    record PaymentSucceededEvent(UUID orderId) implements PaymentSagaEvent {}

    @JsonTypeName("PaymentFailed")
    record PaymentFailedEvent(UUID orderId, String reason) implements PaymentSagaEvent {}
}
