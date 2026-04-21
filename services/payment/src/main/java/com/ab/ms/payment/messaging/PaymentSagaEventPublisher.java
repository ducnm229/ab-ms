package com.ab.ms.payment.messaging;

import com.ab.ms.payment.saga.PaymentSagaEvent;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

@Slf4j
@Component
public class PaymentSagaEventPublisher {

    public static final String ORDER_SAGA_EVENTS_OUT_BINDING = "paymentSagaEvents-out-0";

    private final StreamBridge streamBridge;

    public PaymentSagaEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publish(PaymentSagaEvent event) {
        log.info("Publishing event {} with binding name: {}", event, ORDER_SAGA_EVENTS_OUT_BINDING);
        Message<PaymentSagaEvent> message = MessageBuilder.withPayload(event)
                .setHeaderIfAbsent(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE)
                .build();
        streamBridge.send(ORDER_SAGA_EVENTS_OUT_BINDING, message);
    }
}
