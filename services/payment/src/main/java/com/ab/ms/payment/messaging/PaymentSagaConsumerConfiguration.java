package com.ab.ms.payment.messaging;

import com.ab.ms.payment.entity.PaymentStatus;
import com.ab.ms.payment.dto.ProcessPaymentRequest;
import com.ab.ms.payment.exceptions.IllegalInboundSagaCommandException;
import com.ab.ms.payment.exceptions.InvalidPaymentStatusException;
import com.ab.ms.payment.saga.PaymentSagaCommand;
import com.ab.ms.payment.saga.PaymentSagaEvent;
import com.ab.ms.payment.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/*
 * Consumers for inbound commands
 */
@Slf4j
@Configuration
public class PaymentSagaConsumerConfiguration {

     @Bean(name = "processPaymentCommands")
    Consumer<PaymentSagaCommand> processPaymentCommands(
            PaymentService paymentService,
            PaymentSagaEventPublisher publisher) {
        return command -> {
            log.info("Received processPaymentCommand command: " + command);
            if (command instanceof PaymentSagaCommand.ProcessPayment processPayment) {
                ProcessPaymentRequest request = new ProcessPaymentRequest(
                        processPayment.orderId(),
                        processPayment.customerId(),
                        processPayment.amount(),
                        processPayment.currency());
                var result = paymentService.processPayment(request);
                if (result.status() == PaymentStatus.SUCCEEDED) {
                    publisher.publish(new PaymentSagaEvent.PaymentSucceededEvent(processPayment.orderId()));
                } else if (result.status() == PaymentStatus.FAILED) {
                    publisher.publish(new PaymentSagaEvent.PaymentFailedEvent(
                            processPayment.orderId(),
                            result.failureReason()));
                } else {
                    throw new InvalidPaymentStatusException(result.status().name());
                }
            } else {
                throw new IllegalInboundSagaCommandException(
                        command,
                        PaymentSagaCommand.ProcessPayment.class.getSimpleName());
            }
        };
    }
}
