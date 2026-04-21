package com.ab.ms.order.messaging;

import com.ab.ms.order.entity.Order;
import com.ab.ms.order.saga.OrderSagaCommand;
import com.ab.ms.order.saga.dispatch.InProcessLoggingSagaCommandDispatcher;
import com.ab.ms.order.saga.dispatch.SagaCommandDispatcher;

import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Invokes in-process logging, then publishes the next saga command to broker
 */
@Component
@Primary
@AllArgsConstructor
public class StreamBridgeSagaCommandDispatcher implements SagaCommandDispatcher {

    private final InProcessLoggingSagaCommandDispatcher inProcessLogging;
    private final OrderCommandPublisher orderCommandPublisher;

    @Override
    public void dispatch(Order order, Optional<OrderSagaCommand> command) {
        inProcessLogging.dispatch(order, command);
        command.ifPresent(orderCommandPublisher::publishCommandAfterCommit);
    }
}
