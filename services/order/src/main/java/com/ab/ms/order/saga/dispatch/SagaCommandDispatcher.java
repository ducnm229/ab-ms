package com.ab.ms.order.saga.dispatch;

import com.ab.ms.order.entity.Order;
import com.ab.ms.order.saga.OrderSagaCommand;

import java.util.Optional;

/**
 * Interface for message dispatch;
 * implementer can be simple logging (for initial development) 
 * or publish commands to brokers.
 */
public interface SagaCommandDispatcher {

    void dispatch(Order order, Optional<OrderSagaCommand> command);
}
