package com.ab.ms.order.client;

import com.ab.ms.order.dto.CustomerStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "customer-service",
        url = "${clients.customer-service.url}",
        contextId = "customer-service"
)
public interface CustomerClient {

    @GetMapping("/api/customers/{id}/status")
    CustomerStatus getStatus(@PathVariable("id") Long customerId);
}
