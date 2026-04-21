package com.ab.ms.order.messaging;

import com.ab.ms.order.saga.OrderSagaCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.MimeTypeUtils;

import java.nio.charset.StandardCharsets;

/**
 * Publishes saga commands to broker after the orchestrating transaction commits
 */
@Component
public class OrderCommandPublisher {

    private static Logger log = LoggerFactory.getLogger(OrderCommandPublisher.class);

    private final StreamBridge streamBridge;

    public OrderCommandPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    /**
     * scheduled to send after successful commit
     */
    public void publishCommandAfterCommit(OrderSagaCommand cmd) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                send(cmd);
            }
        });
    }

    private void send(OrderSagaCommand cmd) {
        String bindingName = bindingNameFor(cmd);
        Message<OrderSagaCommand> message = MessageBuilder.withPayload(cmd)
                .setHeader(KafkaHeaders.KEY, cmd.orderId().toString().getBytes(StandardCharsets.UTF_8))
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE)
                .build();
        log.info("Sending command to broker: {} with binding name: {}", cmd, bindingName);
        boolean sent = streamBridge.send(bindingName, message);
        if (!sent) {
            throw new IllegalStateException("StreamBridge returned false for binding " + bindingName);
        }
    }

    private static String bindingNameFor(OrderSagaCommand cmd) {
        return switch (cmd) {
            case OrderSagaCommand.ReserveInventory ignored -> "reserveInventory-out-0";
            case OrderSagaCommand.ReleaseInventory ignored -> "releaseInventory-out-0";
            case OrderSagaCommand.ProcessPayment ignored -> "processPayment-out-0";
            case OrderSagaCommand.ConfirmOrder ignored -> "confirmOrder-out-0";
        };
    }
}
