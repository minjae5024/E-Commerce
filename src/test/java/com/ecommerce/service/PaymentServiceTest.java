package com.ecommerce.service;

import com.ecommerce.domain.Order;
import com.ecommerce.domain.User;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.PaymentRepository;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("결제 처리 성공")
    void Payment_success() {
        // given
        String userEmail = "testUser@test.com";
        Long orderId = 1L;

        User user = spy(User.builder().points(50000).build());
        ReflectionTestUtils.setField(user, "id", 1L);

        Order order = spy(Order.createOrder(user, Collections.emptyList()));
        ReflectionTestUtils.setField(order, "totalPrice", 20000);

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(user));
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when
        paymentService.processInternalPayment(userEmail, orderId);

        // then
        verify(user).usePoints(20000);
        verify(order).completePayment();
        verify(paymentRepository).save(any(com.ecommerce.domain.Payment.class));
    }

    @Test
    @DisplayName("결제 실패 - 포인트 부족")
    void Payment_fail_notEnoughPoints() {
        // given
        String userEmail = "testUser@test.com";
        Long orderId = 1L;

        User user = User.builder().points(10000).build();
        ReflectionTestUtils.setField(user, "id", 1L);

        Order order = Order.createOrder(user, Collections.emptyList());
        ReflectionTestUtils.setField(order, "totalPrice", 20000);

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(user));
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> paymentService.processInternalPayment(userEmail, orderId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("포인트가 부족합니다.");
    }

    @Test
    @DisplayName("결제 실패 - 이미 처리된 주문")
    void Payment_fail_alreadyPaid() {
        // given
        String userEmail = "testUser@test.com";
        Long orderId = 1L;

        User user = User.builder().points(50000).build();
        ReflectionTestUtils.setField(user, "id", 1L);

        Order order = Order.createOrder(user, Collections.emptyList());
        order.completePayment();

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(user));
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> paymentService.processInternalPayment(userEmail, orderId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("결제 가능한 상태의 주문이 아닙니다.");
    }
}
