package com.ab.ms.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ReserveInventoryRequest(
        @NotNull UUID orderId,
        @NotBlank String productId,
        @NotNull @Positive Integer quantity) {}
