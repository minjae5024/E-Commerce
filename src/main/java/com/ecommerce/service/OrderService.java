package com.ecommerce.service;

import com.ecommerce.domain.*;
import com.ecommerce.dto.OrderDetailResponseDto;
import com.ecommerce.dto.OrderResponseDto;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository; 

    @Transactional
    public Long createOrderFromCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("장바구니를 찾을 수 없습니다"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("장바구니가 비어있습니다");
        }

        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    
                    Product product = productRepository.findWithLockById(cartItem.getProduct().getId())
                            .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다"));
                    
                    return OrderItem.createOrderItem(product, cartItem.getQuantity());
                })
                .collect(Collectors.toList());

        Order order = Order.createOrder(user, orderItems);
        orderRepository.save(order);
        cart.clearItems();

        return order.getId();
    }

    public Page<OrderResponseDto> findUserOrders(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));
        Page<Order> orders = orderRepository.findByUser(user, pageable);
        return orders.map(OrderResponseDto::new);
    }

    public OrderDetailResponseDto findOrderDetails(String userEmail, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("조회할 권한이 없습니다");
        }

        return new OrderDetailResponseDto(order);
    }

    @Transactional
    public void cancelOrder(String userEmail, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("취소할 권한이 없습니다");
        }

        order.cancel();
    }
}