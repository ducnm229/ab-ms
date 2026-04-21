package com.ab.ms.inventory.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReleaseInventoryRequest(@NotNull UUID orderId) {}
