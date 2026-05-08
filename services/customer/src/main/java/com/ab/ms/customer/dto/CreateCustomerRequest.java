package com.ab.ms.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(
    name = "Customer",
    description = "Schema to hold customer registration request data"
)
@Data
public class CreateCustomerRequest {
    @Schema(
        description = "Customer name", example = "Alan Baker"
    )
    @NotEmpty(message = "Required field: name")
    @Size(min = 5, max = 30, message = "Length of name must be between 5 and 30 characters")
    private String name;

    @Schema(
        description = "Customer email address", example = "abel@email.com"
    )
    @NotEmpty(message = "Required field: email")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(
        description = "Customer mobile number", example = "3334445555"
    )
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    private String mobileNumber;
}
