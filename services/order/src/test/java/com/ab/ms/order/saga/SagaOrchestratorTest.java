package com.ab.ms.order.saga;

import com.ab.ms.order.entity.Order;
import com.ab.ms.order.entity.OrderStatus;
import com.ab.ms.order.exceptions.InvalidOrderSagaTransitionException;
import com.ab.ms.order.repository.OrderRepository;
import com.ab.ms.order.saga.dispatch.SagaCommandDispatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SagaOrchestratorTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SagaCommandDispatcher sagaCommandDispatcher;

    @InjectMocks
    private SagaOrchestrator sagaOrchestrator;

    @Test
    void handleEvent_firstTimeTransition_updatesStatusAndDispatchesNextCommand() {
        Order order = orderWithStatus(OrderStatus.INVENTORY_RESERVED);
        OrderSagaEvent event = new OrderSagaEvent.PaymentSucceededEvent(order.getId());

        Order result = sagaOrchestrator.handleEvent(order, event);

        assertEquals(OrderStatus.PAYMENT_COMPLETED, result.getStatus());
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Optional<OrderSagaCommand>> commandCaptor =
                (ArgumentCaptor<Optional<OrderSagaCommand>>) (ArgumentCaptor<?>) ArgumentCaptor.forClass(Optional.class);
        verify(sagaCommandDispatcher).dispatch(any(Order.class), commandCaptor.capture());
        assertEquals(new OrderSagaCommand.ConfirmOrder(order.getId()), commandCaptor.getValue().orElseThrow());
    }

    @Test
    void handleEvent_duplicateReplay_returnsNoOpAndDoesNotDispatchAgain() {
        Order order = orderWithStatus(OrderStatus.PAYMENT_COMPLETED);
        OrderSagaEvent event = new OrderSagaEvent.PaymentSucceededEvent(order.getId());

        Order result = sagaOrchestrator.handleEvent(order, event);

        assertEquals(OrderStatus.PAYMENT_COMPLETED, result.getStatus());
        verify(sagaCommandDispatcher, never()).dispatch(any(Order.class), any());
    }

    @Test
    void handleEvent_conflictingLateEvent_throwsInvalidTransition() {
        Order order = orderWithStatus(OrderStatus.INVENTORY_RESERVED);
        OrderSagaEvent event = new OrderSagaEvent.InventoryFailedEvent(order.getId(), "too late", null);

        assertThrows(InvalidOrderSagaTransitionException.class, () -> sagaOrchestrator.handleEvent(order, event));
        verify(sagaCommandDispatcher, never()).dispatch(any(Order.class), any());
    }

    private static Order orderWithStatus(OrderStatus status) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setCustomerId(10L);
        order.setTotalAmount(BigDecimal.TEN);
        order.setStatus(status);
        return order;
    }
}
