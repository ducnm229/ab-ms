package com.ab.ms.customer.dto;

public record CustomerRegisteredEventDto(
        String name,
        String email,
        String mobileNumber
) {
}
