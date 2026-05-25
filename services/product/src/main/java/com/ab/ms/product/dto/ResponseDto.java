package com.ab.ms.product.dto;

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
