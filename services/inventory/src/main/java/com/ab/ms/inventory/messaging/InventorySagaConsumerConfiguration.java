package com.ab.ms.inventory.messaging;

import com.ab.ms.inventory.exceptions.IllegalInboundSagaCommandException;
import com.ab.ms.inventory.saga.InventorySagaCommand;
import com.ab.ms.inventory.saga.InventorySagaEvent;
import com.ab.ms.inventory.service.InventoryService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * Consumers for inbound commands
 */
@Slf4j
@Configuration
public class InventorySagaConsumerConfiguration {

    @Bean
    Consumer<InventorySagaCommand> reserveInventoryCommands(
            InventoryService inventoryService,
            InventorySagaEventPublisher publisher) {
        return command -> {
            log.info("Received command: {}", command);
            if (command instanceof InventorySagaCommand.ReserveInventory(
                    java.util.UUID orderId, java.util.List<InventoryItemMessage> items
            )) {
                var result = inventoryService.reserveInventory(orderId, items);
                log.info("Inventory reservation result: {}", result);
                if (result.success()) {
                    publisher.publish(
                            new InventorySagaEvent.InventoryReservedEvent(orderId, result.reservedItems()));
                } else {
                    publisher.publish(new InventorySagaEvent.InventoryFailedEvent(
                            orderId, result.reason(), result.failedItems()));
                }
            } else {
                throw new IllegalInboundSagaCommandException(command, InventorySagaCommand.ReserveInventory.class.getSimpleName());
            }
        };
    }

    @Bean
    Consumer<InventorySagaCommand> releaseInventoryCommands(
            InventoryService inventoryService,
            InventorySagaEventPublisher publisher) {
        return command -> {
            log.info("Received command: {}", command);
            if (command instanceof InventorySagaCommand.ReleaseInventory(java.util.UUID orderId)) {
                inventoryService.releaseInventory(orderId);
                publisher.publish(new InventorySagaEvent.InventoryReleasedEvent(orderId));
            } else {
                throw new IllegalInboundSagaCommandException(command, InventorySagaCommand.ReleaseInventory.class.getSimpleName());
            }
        };
    }
}
