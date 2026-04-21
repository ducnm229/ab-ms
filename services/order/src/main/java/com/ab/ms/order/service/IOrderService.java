package com.ab.ms.order.service;

import com.ab.ms.order.dto.CreateOrderRequestDto;
import com.ab.ms.order.dto.OrderDto;

import java.util.UUID;

public interface IOrderService {

    OrderDto createOrder(CreateOrderRequestDto createOrderRequestDto);

    OrderDto getOrderById(UUID orderId);
}
