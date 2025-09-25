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
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("이 주문을 결제할 권한이 없습니다.");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("결제 가능한 상태의 주문이 아닙니다.");
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
