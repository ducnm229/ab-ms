package com.ab.ms.inventory.exceptions;

import com.ab.ms.inventory.saga.InventorySagaCommand;

public class IllegalInboundSagaCommandException extends NonRetryableSagaException {

    public IllegalInboundSagaCommandException(InventorySagaCommand command, String channel) {
        super("Illegal inbound saga command (%s) on %s channel"
            .formatted(command.getClass().getSimpleName(), channel));
    }
}
