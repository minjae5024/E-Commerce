package com.ecommerce.service;

import com.ecommerce.domain.*;
import com.ecommerce.dto.OrderDetailResponseDto;
import com.ecommerce.dto.OrderResponseDto;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
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

    @Transactional
    public Long createOrderFromCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userEmail));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Cart not found for user"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Create OrderItems from CartItems
        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> OrderItem.createOrderItem(cartItem.getProduct(), cartItem.getQuantity()))
                .collect(Collectors.toList());

        // Create Order
        Order order = Order.createOrder(user, orderItems);

        // Save Order
        orderRepository.save(order);

        // Clear Cart
        cart.clearItems();

        return order.getId();
    }

    public Page<OrderResponseDto> findUserOrders(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userEmail));
        Page<Order> orders = orderRepository.findByUser(user, pageable);
        return orders.map(OrderResponseDto::new);
    }

    public OrderDetailResponseDto findOrderDetails(String userEmail, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("User does not have permission to view this order");
        }

        return new OrderDetailResponseDto(order);
    }

    @Transactional
    public void cancelOrder(String userEmail, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("User does not have permission to cancel this order");
        }

        if (order.getStatus() != OrderStatus.ORDERED) {
            throw new IllegalStateException("Order cannot be canceled if it's not in ORDERED state.");
        }

        order.cancel();
    }
}
