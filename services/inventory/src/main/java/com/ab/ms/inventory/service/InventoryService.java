package com.ab.ms.inventory.service;

import com.ab.ms.inventory.entity.InventoryItem;
import com.ab.ms.inventory.entity.InventoryReservation;
import com.ab.ms.inventory.entity.ReservationStatus;
import com.ab.ms.inventory.messaging.InventoryItemMessage;
import com.ab.ms.inventory.repository.InventoryItemRepository;
import com.ab.ms.inventory.repository.InventoryReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
    private final InventoryItemRepository itemRepository;
    private final InventoryReservationRepository reservationRepository;

    public InventoryService(
            InventoryItemRepository itemRepository,
            InventoryReservationRepository reservationRepository) {
        this.itemRepository = itemRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Reserves stock for a single product line (delegates to the batched implementation).
     */
    @Transactional
    public ReserveInventoryResult reserveInventory(UUID orderId, String productId, int quantity) {
        return reserveInventory(orderId, List.of(new InventoryItemMessage(productId, quantity)));
    }

    /**
     * Reserves stock for all lines in one transaction (atomic reservation: all lines or none).
     */
    @Transactional
    public ReserveInventoryResult reserveInventory(UUID orderId, List<InventoryItemMessage> items) {
        if (CollectionUtils.isEmpty(items)) {
            return ReserveInventoryResult.failure("No items to reserve", List.of());
        }

        // if reservation already exists, just return it
        List<InventoryReservation> existingReserved =
                reservationRepository.findByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);
        if (!existingReserved.isEmpty()) {
            return ReserveInventoryResult.ok(toLines(existingReserved));
        }

        // Treemap for deterministic lock order
        Map<String, Integer> merged = new TreeMap<>();
        for (InventoryItemMessage line : items) {
            merged.merge(line.productId(), line.quantity(), Integer::sum);
        }
        List<InventoryItemMessage> sortedLines = merged.entrySet().stream()
                .map(e -> new InventoryItemMessage(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(InventoryItemMessage::productId))
                .toList();

        List<InventoryItemMessage> failed = new ArrayList<>();
        List<InventoryItemMessage> reserved = new ArrayList<>();

        boolean hasFailure = false;
        for (InventoryItemMessage line : sortedLines) {
            InventoryItem stock = itemRepository
                    .findByProductIdForUpdate(line.productId())
                    .orElse(null);

            if (stock == null || stock.getAvailableQuantity() < line.quantity()) {
                failed.add(line);
                hasFailure = true;
                log.info("Reserving failed for item line: {}", line);
                continue;
            }

            if (!hasFailure) {
                stock.setAvailableQuantity(stock.getAvailableQuantity() - line.quantity());

                InventoryReservation reservation = new InventoryReservation();
                reservation.setOrderId(orderId);
                reservation.setProductId(line.productId());
                reservation.setQuantity(line.quantity());
                reservation.setStatus(ReservationStatus.RESERVED);

                reservationRepository.save(reservation);

                reserved.add(line);
            }
        }

        // Rollback if any failure occurred
        if (!failed.isEmpty()) {
            // Mark transaction for rollback explicitly (optional but clearer)
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ReserveInventoryResult.failure("Insufficient stock", failed);
        }
        log.info("Reserved list: {}", reserved);

        return ReserveInventoryResult.ok(reserved);
    }

    /**
     * Restores stock for active reservations and marks them released
     */
    @Transactional
    public void releaseInventory(UUID orderId) {
        List<InventoryReservation> active =
                reservationRepository.findByOrderIdAndStatus(orderId, ReservationStatus.RESERVED);
        for (InventoryReservation reservation : active) {
            log.info("Releasing stock for {}", reservation);
            InventoryItem stock =
                    itemRepository.findByProductIdForUpdate(reservation.getProductId()).orElseThrow();
            stock.setAvailableQuantity(stock.getAvailableQuantity() + reservation.getQuantity());
            reservation.setStatus(ReservationStatus.RELEASED);
        }
    }

    private static List<InventoryItemMessage> toLines(List<InventoryReservation> reservations) {
        return reservations.stream()
                .map(r -> new InventoryItemMessage(r.getProductId(), r.getQuantity()))
                .toList();
    }
}
