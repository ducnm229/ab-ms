package com.ab.ms.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(
    name = "Customer",
    description = "Schema to hold customer information"
)
@Data
public class CustomerResponse {
    @Schema(
            description = "Customer ID",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Customer name",
            example = "Alan Baker"
    )
    private String name;

    @Schema(
            description = "Customer email address",
            example = "abel@email.com"
    )
    private String email;

    @Schema(
            description = "Customer mobile number",
            example = "3334445555"
    )
    private String mobileNumber;

    @Schema(
            description = "Customer status",
            example = "true"
    )
    private boolean active;
}
