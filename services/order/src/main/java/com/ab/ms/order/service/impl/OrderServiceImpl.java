package com.ab.ms.order.service.impl;

import com.ab.ms.order.dto.CreateOrderRequestDto;
import com.ab.ms.order.dto.OrderDto;
import com.ab.ms.order.domain.Product;
import com.ab.ms.order.domain.ProductCatalog;
import com.ab.ms.order.entity.Order;
import com.ab.ms.order.entity.OrderItem;
import com.ab.ms.order.entity.OrderStatus;
import com.ab.ms.order.exceptions.ResourceNotFoundException;
import com.ab.ms.order.exceptions.UnknownProductException;
import com.ab.ms.order.mapper.OrderMapper;
import com.ab.ms.order.repository.OrderRepository;
import com.ab.ms.order.saga.OrderSagaEvent;
import com.ab.ms.order.saga.SagaOrchestrator;
import com.ab.ms.order.service.CustomerValidationService;
import com.ab.ms.order.service.IOrderService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private OrderRepository orderRepository;
    private SagaOrchestrator sagaOrchestrator;
    private ProductCatalog productCatalog;
    private CustomerValidationService customerValidationService;

    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderRequestDto createOrderRequestDto) {
        customerValidationService.validateCustomerOrThrow(createOrderRequestDto.getCustomerId());

        List<OrderItem> pricedItems = createOrderRequestDto.getItems().stream()
                .map(item -> {
                    Product product = productCatalog.get(item.getProductId());
                    if (product == null) {
                        throw new UnknownProductException(item.getProductId());
                    }
                    return new OrderItem(item.getProductId(), item.getQuantity(), product.price());
                })
                .toList();

        BigDecimal totalAmount = pricedItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setCustomerId(createOrderRequestDto.getCustomerId());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.CREATED);
        order.setItems(pricedItems);
        order = orderRepository.save(order);
        order = sagaOrchestrator.handleEvent(order, new OrderSagaEvent.OrderCreatedEvent(order.getId()));
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId.toString()));
        return OrderMapper.toDto(order);
    }
}
