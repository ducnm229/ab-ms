package com.ab.ms.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(
    name = "Error Response",
    description = "Schema to hold error response information"
)
@Data
@AllArgsConstructor
public class ErrorResponseDto {

    @Schema(
        description = "api path invoked by client"
    )
    private String apiPath;

    @Schema(
        description = "Error status code", example = "404/500/etc."
    )
    private String errorCode;

    @Schema(
        description = "Error message"
    )
    private String errorMessage;

    @Schema(
        description = "Timestamp of error"
    )
    private LocalDateTime errorTime;
}
