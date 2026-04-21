package com.ab.ms.order.client;

import com.ab.ms.order.dto.CustomerStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * Unused: not using Feign fallback
 */
@Slf4j
// @Component — disabled
public class CustomerClientFallback implements CustomerClient {

    @Override
    public CustomerStatus getStatus(Long customerId) {
        log.warn("Customer client fallback triggered for customerId={}. Returning fail-closed status.", customerId);
        return new CustomerStatus(false, false);
    }
}
