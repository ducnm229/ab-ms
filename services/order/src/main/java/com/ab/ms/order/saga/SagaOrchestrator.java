package com.ab.ms.order.saga;

import com.ab.ms.order.entity.Order;
import com.ab.ms.order.exceptions.MismatchedOrderIdSagaException;
import com.ab.ms.order.exceptions.OrderNotFoundSagaException;
import com.ab.ms.order.exceptions.SagaEventNullOrderIdException;
import com.ab.ms.order.repository.OrderRepository;
import com.ab.ms.order.saga.dispatch.SagaCommandDispatcher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Saga orchestrator: Order aggregate is source of truth; drives state and next outbound command
 */
@Component
@RequiredArgsConstructor
public class SagaOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(SagaOrchestrator.class);
    private final OrderRepository orderRepository;
    private final SagaCommandDispatcher sagaCommandDispatcher;

    /**
     * Applies one incoming event: validates transition, persists new status, dispatches next command (if any).
     */
    @Transactional
    public Order handleEvent(OrderSagaEvent event) {
        validateOrderIdNotNull(event);
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundSagaException(event.orderId()));
        applyTransition(order, SagaTransitions.resolve(order, event));
        return order;
    }

    // for internal path eg. after creating order
    @Transactional
    public Order handleEvent(Order order, OrderSagaEvent event) {
        validateOrderIdNotNull(event);
        if (!order.getId().equals(event.orderId())) {
            throw new MismatchedOrderIdSagaException(event.orderId(), order.getId());
        }
        log.info("Processing order status transitioning for order ID: {}", event.orderId());
        applyTransition(order, SagaTransitions.resolve(order, event));
        return order;
    }

    @Transactional
    private void applyTransition(Order order, SagaTransitionResult result) {
        if (result instanceof SagaTransitionResult.NoOpDuplicate) {
            return;
        }
        SagaStep step = ((SagaTransitionResult.ApplyTransition) result).step();
        log.info("Transitioning order to next step: {}", step);
        order.setStatus(step.nextStatus());
        sagaCommandDispatcher.dispatch(order, step.nextCommand());
    }

    private void validateOrderIdNotNull(OrderSagaEvent event) {
        if (event.orderId() == null) {
            throw new SagaEventNullOrderIdException(event);
        }
    }
}
