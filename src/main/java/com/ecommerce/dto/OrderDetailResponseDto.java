package com.ecommerce.dto;

import com.ecommerce.domain.Order;
import com.ecommerce.domain.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderDetailResponseDto {
    private Long orderId;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private int totalPrice;
    private List<OrderItemResponseDto> orderItems;

    public OrderDetailResponseDto(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.totalPrice = order.getTotalPrice();
        this.orderItems = order.getOrderItems().stream()
                .map(OrderItemResponseDto::new)
                .collect(Collectors.toList());
    }
}
