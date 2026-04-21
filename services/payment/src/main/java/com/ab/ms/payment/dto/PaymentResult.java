package com.ab.ms.payment.dto;

import com.ab.ms.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResult(
        UUID paymentId,
        UUID orderId,
        Long customerId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        String failureReason) {
}
