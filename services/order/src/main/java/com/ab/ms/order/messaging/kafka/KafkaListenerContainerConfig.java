package com.ab.ms.order.messaging.kafka;

import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

import com.ab.ms.order.exceptions.NonRetryableSagaException;

import lombok.extern.slf4j.Slf4j;

/**
 * Kafka listener container configuration for saga-related consumers.
 * Configures message-layer retry/backoff
 */
@Slf4j
@Configuration
public class KafkaListenerContainerConfig {
    
    @Bean
    public DefaultErrorHandler orderKafkaErrorHandler() {
        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);

        backOff.setInitialInterval(500L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(5000L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                // Called after retries are exhausted
                (record, ex) -> log.warn(
                        "Kafka listener giving up after retries; topic={}, partition={}, offset={}: {}",
                        record.topic(),
                        record.partition(),
                        record.offset(),
                        ex.toString()
                ),
                backOff
        );

        // Exceptions that should NOT be retried go here
        errorHandler.addNotRetryableExceptions(
                NonRetryableSagaException.class
        );

        return errorHandler;
    }

    @Bean
    public ListenerContainerCustomizer<AbstractMessageListenerContainer<?, ?>>
    orderSagaKafkaListenerContainerCustomizer(DefaultErrorHandler errorHandler) {

        return (container, destinationName, group) -> {
            container.setCommonErrorHandler(errorHandler);
        };
    }
}
