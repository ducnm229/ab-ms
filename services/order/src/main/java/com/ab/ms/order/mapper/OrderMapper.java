package com.ab.ms.order.mapper;

import com.ab.ms.order.dto.OrderDto;
import com.ab.ms.order.dto.OrderItemDto;
import com.ab.ms.order.entity.Order;
import com.ab.ms.order.entity.OrderItem;

import java.util.List;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setCustomerId(order.getCustomerId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setItems(toDtoItems(order.getItems()));
        return dto;
    }

    private static List<OrderItemDto> toDtoItems(List<OrderItem> items) {
        return items.stream()
                .map(item -> {
                    OrderItemDto dto = new OrderItemDto();
                    dto.setProductId(item.getProductId());
                    dto.setQuantity(item.getQuantity());
                    return dto;
                })
                .toList();
    }
}
