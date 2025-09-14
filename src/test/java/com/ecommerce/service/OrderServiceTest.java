package com.ecommerce.service;

import com.ecommerce.domain.*;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_success() {
        // given
        String userEmail = "testUser@test.com";
        User user = User.builder().email(userEmail).build();
        ReflectionTestUtils.setField(user, "id", 1L);

        Product product = Product.builder().name("testProduct").price(10000).stockQuantity(100).build();
        Cart cart = Cart.createCart(user);
        CartItem cartItem = CartItem.createCartItem(cart, product, 2);
        ReflectionTestUtils.setField(cart, "cartItems", new java.util.ArrayList<>(java.util.List.of(cartItem)));

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(user));
        given(cartRepository.findByUserId(user.getId())).willReturn(Optional.of(cart));

        // when
        orderService.createOrderFromCart(userEmail);

        // then
        verify(orderRepository).save(any(Order.class));
        assertThat(cart.getCartItems()).isEmpty();
    }

    @Test
    @DisplayName("주문 생성 실패 - 장바구니 비었음")
    void createOrder_fail_emptyCart() {
        // given
        String userEmail = "testUser@test.com";
        User user = User.builder().email(userEmail).build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Cart cart = Cart.createCart(user);

        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(user));
        given(cartRepository.findByUserId(user.getId())).willReturn(Optional.of(cart));

        // when & then
        assertThatThrownBy(() -> orderService.createOrderFromCart(userEmail))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cart is empty");
    }

    @Test
    @DisplayName("주문 취소 성공")
    void cancelOrder_success() {
        // given
        String userEmail = "testUser@test.com";
        User user = User.builder().email(userEmail).build();
        ReflectionTestUtils.setField(user, "id", 1L);

        Product product = spy(Product.builder().name("testProduct").price(10000).stockQuantity(98).build());
        OrderItem orderItem = OrderItem.createOrderItem(product, 2);
        Order order = Order.createOrder(user, Collections.singletonList(orderItem));
        order.completePayment();

        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        // when
        orderService.cancelOrder(userEmail, 1L);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(product).addStock(2);
    }
}

