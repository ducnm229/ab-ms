package com.ab.ms.order.messaging;

import com.ab.ms.order.saga.OrderSagaEvent;
import com.ab.ms.order.saga.SagaOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderSagaEventHandler {

    private final SagaOrchestrator sagaOrchestrator;

    @Transactional
    public void handle(OrderSagaEvent event) {
        sagaOrchestrator.handleEvent(event);
    }
}
