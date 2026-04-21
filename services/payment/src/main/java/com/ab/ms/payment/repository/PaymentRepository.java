package com.ab.ms.payment.repository;

import com.ab.ms.payment.entity.Payment;
import com.ab.ms.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByOrderId(UUID orderId);

    Optional<Payment> findFirstByOrderIdAndStatus(UUID orderId, PaymentStatus status);
}
