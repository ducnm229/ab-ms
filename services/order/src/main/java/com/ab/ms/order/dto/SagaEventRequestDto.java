package com.ab.ms.order.dto;

import com.ab.ms.order.saga.OrderSagaEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Polymorphic saga event body. Set {@code type} to one of:
 * OrderCreated, InventoryReserved, InventoryFailed, PaymentSucceeded, PaymentFailed, InventoryReleased, OrderConfirmed.
 */
@Data
public class SagaEventRequestDto {

    @NotNull(message = "Saga event is required")
    @Schema(
            description = "Polymorphic event; include discriminator property \"type\"",
            example = "{\"type\":\"InventoryReserved\",\"reservedItems\":[{\"productId\":\"SKU-1\",\"quantity\":2},{\"productId\":\"SKU-2\",\"quantity\":1}]}"
    )
    private OrderSagaEvent event;
}
