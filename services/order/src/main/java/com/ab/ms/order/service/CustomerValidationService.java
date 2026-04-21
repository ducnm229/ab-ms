package com.ab.ms.order.service;

import com.ab.ms.order.client.CustomerClientWrapper;
import com.ab.ms.order.dto.CustomerStatus;
import com.ab.ms.order.exceptions.InvalidCustomerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerValidationService {

    private final CustomerClientWrapper customerClientWrapper;

    public void validateCustomerOrThrow(Long customerId) {
        CustomerStatus status = customerClientWrapper.getStatus(customerId);
        if (!status.exists() || !status.active()) {
            log.warn("Customer validation failed before saga starts for customerId={}. exists={}, active={}",
                    customerId, status.exists(), status.active());
            throw new InvalidCustomerException(customerId);
        }
    }
}
