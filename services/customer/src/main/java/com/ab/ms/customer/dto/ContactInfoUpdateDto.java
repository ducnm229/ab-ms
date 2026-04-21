package com.ab.ms.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(
    name = "Customer Contact Info",
    description = "Schema to hold customer contact info"
)
@Data
public class ContactInfoUpdateDto {

    @Schema(
        description = "Customer ID", example = "1"
    )
    private Long id;

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
