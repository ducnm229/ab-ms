package com.ab.ms.order.saga.dispatch;

import com.ab.ms.order.entity.Order;
import com.ab.ms.order.saga.OrderSagaCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/*
 * Simple implementation of SagaCommandDispatcher that logs commands.
 */
@Component
public class InProcessLoggingSagaCommandDispatcher implements SagaCommandDispatcher {

    private static final Logger log = LoggerFactory.getLogger(InProcessLoggingSagaCommandDispatcher.class);

    @Override
    public void dispatch(Order order, Optional<OrderSagaCommand> command) {
        if (command.isEmpty()) {
            return;
        }
        OrderSagaCommand cmd = command.get();
        log.info(
                "Saga command={} orderId={} customerId={} statusAfterSave={}",
                cmd,
                order.getId(),
                order.getCustomerId(),
                order.getStatus()
        );
    }
}
