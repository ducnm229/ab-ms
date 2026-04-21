package com.ab.ms.payment.service;

import com.ab.ms.payment.entity.Payment;
import com.ab.ms.payment.entity.PaymentStatus;
import com.ab.ms.payment.dto.PaymentResult;
import com.ab.ms.payment.dto.ProcessPaymentRequest;
import com.ab.ms.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResult processPayment(ProcessPaymentRequest request) {
        // idempotency: if payment already succeeded, return the result immediately
        PaymentResult result = paymentRepository.findFirstByOrderIdAndStatus(request.orderId(), PaymentStatus.SUCCEEDED)
                .map(this::toResult)
                .orElseGet(() -> createAndProcessPayment(request));
        if (result.status() == PaymentStatus.SUCCEEDED) {
            log.info(
                    "Payment succeeded for order {}: charged {} {}",
                    result.orderId(),
                    result.amount(),
                    result.currency());
        }
        return result;
    }

    private PaymentResult createAndProcessPayment(ProcessPaymentRequest request) {
        log.info("Processing Payment Request: {}", request.toString());
        PaymentStatus finalStatus = shouldSucceed(request.orderId()) ? PaymentStatus.SUCCEEDED : PaymentStatus.FAILED;
        log.info("Payment Status: {}", finalStatus);
        Payment payment = Payment.builder()
                .orderId(request.orderId())
                .customerId(request.customerId())
                .amount(request.amount())
                .currency(request.currency().toUpperCase())
                .status(finalStatus)
                .failureReason(finalStatus == PaymentStatus.FAILED ? "Payment declined by deterministic rule" : null)
                .build();
        
        try {
            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment saved: {}", savedPayment);
            return toResult(savedPayment);
        } catch (DataIntegrityViolationException ex) {
            // idempotency: concurrency-safe enforced at DB level by partial index
            if (finalStatus == PaymentStatus.SUCCEEDED) {
                return paymentRepository
                        .findFirstByOrderIdAndStatus(request.orderId(), PaymentStatus.SUCCEEDED)
                        .map(this::toResult)
                        .orElseThrow(() -> ex);
            }
            throw ex;
        }
    }

    private boolean shouldSucceed(UUID orderId) {
        return (orderId.hashCode() & 1) == 0;
    }

    private PaymentResult toResult(Payment payment) {
        return new PaymentResult(
                payment.getId(),
                payment.getOrderId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getFailureReason());
    }
}
