package com.ecommerce.service;

import com.ecommerce.domain.*;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.PaymentRepository;
import com.ecommerce.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public Long processInternalPayment(String userEmail, Long orderId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userEmail));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User does not have permission to pay for this order.");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Order is not in a state to be paid.");
        }

        int totalPrice = order.getTotalPrice();

        user.usePoints(totalPrice);

        order.completePayment();

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod("INTERNAL_POINTS")
                .amount(totalPrice)
                .build();
        paymentRepository.save(payment);

        return payment.getId();
    }
}
