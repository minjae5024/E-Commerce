package com.ecommerce.dto;

import com.ecommerce.domain.Cart;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CartResponseDto {
    private Long cartId;
    private List<CartItemResponseDto> cartItems;
    private int totalPrice;

    public CartResponseDto(Cart cart) {
        this.cartId = cart.getId();
        this.cartItems = cart.getCartItems().stream()
                .map(CartItemResponseDto::new)
                .collect(Collectors.toList());
        this.totalPrice = calculateTotalPrice();
    }

    private int calculateTotalPrice() {
        return cartItems.stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
