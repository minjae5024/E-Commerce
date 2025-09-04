package com.ecommerce.dto;

import com.ecommerce.domain.OrderItem;
import lombok.Getter;

@Getter
public class OrderItemResponseDto {
    private String productName;
    private int orderPrice;
    private int quantity;

    public OrderItemResponseDto(OrderItem orderItem) {
        this.productName = orderItem.getProduct().getName();
        this.orderPrice = orderItem.getOrderPrice();
        this.quantity = orderItem.getQuantity();
    }
}
