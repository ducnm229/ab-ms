package com.ab.ms.accounts.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CardDto {

    @NotEmpty(message = "Mobile number is required")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @NotEmpty(message = "Card number is required")
    @Pattern(regexp="(^$|[0-9]{12})",message = "Card number must be 12 digits")
    private String cardNumber;

    @NotEmpty(message = "Card type is required")
    private String cardType;

    @Positive
    private Long totalLimit;

    @PositiveOrZero
    private Long amountUsed;

    @PositiveOrZero
    private Long availableAmount;
}
