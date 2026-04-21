package com.ab.ms.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {

    @NotNull(message = "Product ID is required")
    private String productId;

    @Positive(message = "Quantity must be greater than 0")
    private int quantity;
}
