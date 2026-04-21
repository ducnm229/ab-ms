package com.ab.ms.payment.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InvalidDataErrorDto extends ResponseDto {

    private Map<String, String> validationErrors;

    public InvalidDataErrorDto(String responseCode, Map<String, String> validationErrors, LocalDateTime timestamp) {
        super(responseCode, "Invalid input data", timestamp);
        this.validationErrors = validationErrors;
    }
}
