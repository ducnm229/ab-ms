package com.ab.ms.order.messaging;

import com.ab.ms.order.saga.OrderSagaEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * Consumer for all inbound saga events
 */
@Configuration
public class OrderSagaEventConsumerConfiguration {

    @Bean
    public Consumer<OrderSagaEvent> orderSagaEvents(OrderSagaEventHandler handler) {
        return handler::handle;
    }
}
