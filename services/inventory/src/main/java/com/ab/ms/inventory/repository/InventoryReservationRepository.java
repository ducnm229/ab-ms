package com.ab.ms.inventory.repository;

import com.ab.ms.inventory.entity.InventoryReservation;
import com.ab.ms.inventory.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {

    List<InventoryReservation> findByOrderId(UUID orderId);

    List<InventoryReservation> findByOrderIdAndStatus(UUID orderId, ReservationStatus status);
}
