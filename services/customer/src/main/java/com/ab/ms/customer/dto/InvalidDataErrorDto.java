package com.ab.ms.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(
    name = "Invalid input data error",
    description = "Error response for invalid input data"
)
@Data
@AllArgsConstructor
public class InvalidDataErrorDto {

    @Schema(
        description = "Error status code", example = "400 BAD_REQUEST"
    )
    private String errorCode;

    @Schema(
        description = "List of all input errors"
    )
    private Map<String, String> errors;

    @Schema(
        description = "Timestamp of error"
    )
    private LocalDateTime errorTime;
}
