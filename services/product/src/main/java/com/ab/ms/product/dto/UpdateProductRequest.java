package com.ab.ms.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank String name,
        @NotBlank String category,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price) {
}
