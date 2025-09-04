package com.ecommerce.dto;

import com.ecommerce.domain.CartItem;
import lombok.Getter;

@Getter
public class CartItemResponseDto {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private int price;
    private int quantity;

    public CartItemResponseDto(CartItem cartItem) {
        this.cartItemId = cartItem.getId();
        this.productId = cartItem.getProduct().getId();
        this.productName = cartItem.getProduct().getName();
        this.price = cartItem.getProduct().getPrice();
        this.quantity = cartItem.getQuantity();
    }
}
