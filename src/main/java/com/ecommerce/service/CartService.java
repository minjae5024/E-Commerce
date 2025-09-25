package com.ecommerce.service;

import com.ecommerce.domain.*;
import com.ecommerce.dto.CartItemRequestDto;
import com.ecommerce.dto.CartResponseDto;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Long addItemToCart(String userEmail, CartItemRequestDto requestDto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다: " + requestDto.getProductId()));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.createCart(user);
                    return cartRepository.save(newCart);
                });

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (cartItem == null) {
            cartItem = CartItem.createCartItem(cart, product, requestDto.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            cartItem.addQuantity(requestDto.getQuantity());
        }

        return cartItem.getId();
    }

    public CartResponseDto getCartForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.createCart(user);
                    return cartRepository.save(newCart);
                });

        return new CartResponseDto(cart);
    }

    @Transactional
    public void updateCartItemQuantity(String userEmail, Long cartItemId, int quantity) {
        CartItem cartItem = findCartItemForUser(userEmail, cartItemId);
        cartItem.updateQuantity(quantity);
    }

    @Transactional
    public void removeCartItem(String userEmail, Long cartItemId) {
        CartItem cartItem = findCartItemForUser(userEmail, cartItemId);
        cartItemRepository.delete(cartItem);
    }

    private CartItem findCartItemForUser(String userEmail, Long cartItemId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 상품을 찾을 수 없습니다: " + cartItemId));

        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new SecurityException("조회할 권한이 없습니다.");
        }
        return cartItem;
    }
}