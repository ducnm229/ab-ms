package com.ab.ms.inventory.controller;

import com.ab.ms.inventory.dto.InventoryStockResponse;
import com.ab.ms.inventory.dto.ReleaseInventoryRequest;
import com.ab.ms.inventory.dto.ReserveInventoryRequest;
import com.ab.ms.inventory.entity.InventoryItem;
import com.ab.ms.inventory.exceptions.ResourceNotFoundException;
import com.ab.ms.inventory.repository.InventoryItemRepository;
import com.ab.ms.inventory.service.InventoryService;
import com.ab.ms.inventory.service.ReserveInventoryResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
@Validated
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryItemRepository inventoryItemRepository;

    public InventoryController(
            InventoryService inventoryService,
            InventoryItemRepository inventoryItemRepository) {
        this.inventoryService = inventoryService;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @PostMapping("/reserve")
    public ResponseEntity<ReserveInventoryResult> reserve(@Valid @RequestBody ReserveInventoryRequest request) {
        ReserveInventoryResult result =
                inventoryService.reserveInventory(
                        request.orderId(),
                        request.productId(),
                        request.quantity());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/release")
    public ResponseEntity<Void> release(@Valid @RequestBody ReleaseInventoryRequest request) {
        inventoryService.releaseInventory(request.orderId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{productId}")
    public InventoryStockResponse getStock(@PathVariable String productId) {
        InventoryItem item =
                inventoryItemRepository
                        .findByProductId(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Inventory item", "productId", productId));
        return new InventoryStockResponse(item.getProductId(), item.getAvailableQuantity());
    }
}
