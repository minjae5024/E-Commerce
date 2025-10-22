package com.ecommerce.controller;

import com.ecommerce.dto.CartItemRequestDto;
import com.ecommerce.dto.CartResponseDto;
import com.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<Long> addItemToCart(@Valid @RequestBody CartItemRequestDto requestDto, Principal principal) {
        Long cartItemId = cartService.addItem(principal.getName(), requestDto);
        return ResponseEntity.ok(cartItemId);
    }

    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(Principal principal) {
        CartResponseDto cart = cartService.getCart(principal.getName());
        return ResponseEntity.ok(cart);
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<Void> updateCartItemQuantity(@PathVariable Long cartItemId,
                                                       @RequestBody Map<String, Integer> requestBody,
                                                       Principal principal) {
        int quantity = requestBody.get("quantity");
        cartService.updateCartItemQuantity(principal.getName(), cartItemId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long cartItemId, Principal principal) {
        cartService.removeCartItem(principal.getName(), cartItemId);
        return ResponseEntity.noContent().build();
    }
}
