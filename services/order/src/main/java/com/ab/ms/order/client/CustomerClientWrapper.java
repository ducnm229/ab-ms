package com.ab.ms.order.client;

import com.ab.ms.order.dto.CustomerStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
 * Resilience layer - isolates resilience mechanisms from business logic
 */
@Service
@RequiredArgsConstructor
public class CustomerClientWrapper {

    private final CustomerClient customerClient;

    @Retry(name = "customer-service-retry")
    @CircuitBreaker(name = "customer-service")
    public CustomerStatus getStatus(Long customerId) {
        return customerClient.getStatus(customerId);
    }
}
