package com.ab.ms.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResponseDto {
    String responseCode;
    String responseMessage;
    LocalDateTime timestamp;
}
