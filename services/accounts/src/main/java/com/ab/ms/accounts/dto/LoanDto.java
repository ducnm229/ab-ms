package com.ab.ms.accounts.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class LoanDto {

    @NotEmpty(message = "Mobile number is required")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @NotEmpty(message = "Loan number is required")
    @Pattern(regexp="(^$|[0-9]{12})",message = "Loan number must be 12 digits")
    private String loanNumber;

    @NotEmpty(message = "Loan type is required")
    private String loanType;

    @Positive
    private Long totalLoan;

    @PositiveOrZero
    private Long amountPaid;

    @PositiveOrZero
    private Long outstandingAmount;
}
