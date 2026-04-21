package com.ab.ms.accounts.service.client;

import com.ab.ms.accounts.dto.CardDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CardsFallback implements CardsFeignClient {
    @Override
    public ResponseEntity<CardDto> fetchCard(String correlationId, String mobileNumber) {
        return null;
    }
}
