package com.ecommerce;

import com.ecommerce.config.jwt.JwtTokenProvider;
import com.ecommerce.domain.*;
import com.ecommerce.repository.*;
import com.ecommerce.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderProcessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private CartService cartService;

    private String userToken;
    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        productRepository.deleteAll();
        cartRepository.deleteAll();
        cartItemRepository.deleteAll();
        orderRepository.deleteAll();
        paymentRepository.deleteAll();

        user = User.builder()
                .email("testUser@test.com")
                .password(passwordEncoder.encode("testPassword"))
                .name("testUser")
                .role(Role.USER)
                .points(100000)
                .build();
        userRepository.save(user);

        product = Product.builder()
                .name("testProduct")
                .price(10000)
                .stockQuantity(100)
                .description("testDescription")
                .build();
        productRepository.save(product);

        Authentication userAuth = new UsernamePasswordAuthenticationToken(user.getEmail(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        userToken = jwtTokenProvider.generateToken(userAuth);
    }

    @Test
    @DisplayName("주문 및 결제 통합 테스트 - 성공 시나리오")
    void orderAndPayment_success_scenario() throws Exception {
        // 1. Add item to cart
        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("productId", product.getId(), "quantity", 2))))
                .andExpect(status().isOk());

        // 2. Create order from cart
        MvcResult orderResult = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated())
                .andReturn();
        Long orderId = objectMapper.readValue(orderResult.getResponse().getContentAsString(), Long.class);

        // --- Verification after order creation ---
        Order createdOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(createdOrder.getTotalPrice()).isEqualTo(20000);

        // 3. Process payment for the order
        mockMvc.perform(post("/api/payments/internal/" + orderId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // --- Final Verification after payment ---
        // Verify Order status
        Order completedOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.ORDERED);

        // Verify User points
        User finalUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(finalUser.getPoints()).isEqualTo(100000 - 20000);

        // Verify Product stock
        Product finalProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(finalProduct.getStockQuantity()).isEqualTo(100 - 2);

        // Verify Payment record
        Payment payment = paymentRepository.findAll().get(0);
        assertThat(payment.getOrder().getId()).isEqualTo(orderId);
        assertThat(payment.getAmount()).isEqualTo(20000);

        // Verify Cart is empty
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow();
        assertThat(cart.getCartItems()).isEmpty();
    }
}
