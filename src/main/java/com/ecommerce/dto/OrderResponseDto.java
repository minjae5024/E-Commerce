package com.ecommerce.dto;

import com.ecommerce.domain.Order;
import com.ecommerce.domain.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResponseDto {
    private Long orderId;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private int totalPrice;
    private String representativeProductName;

    public OrderResponseDto(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.totalPrice = order.getTotalPrice();
        if (!order.getOrderItems().isEmpty()) {
            this.representativeProductName = order.getOrderItems().get(0).getProduct().getName();
            if (order.getOrderItems().size() > 1) {
                this.representativeProductName += " and " + (order.getOrderItems().size() - 1) + " others";
            }
        }
    }
}
