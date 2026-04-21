package com.ab.ms.inventory.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErrorResponseDto extends ResponseDto {

    private String apiPath;

    public ErrorResponseDto(String responseCode, String responseMessage, LocalDateTime timestamp, String apiPath) {
        super(responseCode, responseMessage, timestamp);
        this.apiPath = apiPath;
    }
}
