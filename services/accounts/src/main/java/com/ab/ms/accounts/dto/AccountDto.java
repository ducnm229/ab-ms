package com.ab.ms.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(
    name = "Account",
    description = "Schema to hold account information"
)
@Data
public class AccountDto {

    @Schema(
        description = "Account number"
    )
    @NotEmpty(message = "Required field: account number")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Account number must be 10 digits")
    private Long accountNumber;

    @Schema(
        description = "Type of the account", example = "Savings"
    )
    @NotEmpty(message = "Required field: account type")
    private String accountType;

    @Schema(
        description = "Address of the bank branch"
    )
    @NotEmpty(message = "Required field: branch address")
    private String branchAddress;
}
