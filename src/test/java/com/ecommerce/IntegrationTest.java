package com.ecommerce;

import com.ecommerce.config.jwt.JwtTokenProvider;
import com.ecommerce.domain.*;
import com.ecommerce.dto.CartItemRequestDto;
import com.ecommerce.repository.*;
import com.ecommerce.service.CartService;
import com.ecommerce.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class IntegrationTest {

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
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;

    private String userToken;
    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        productRepository.deleteAll();

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
    @DisplayName("성공")
    void orderAndPayment_success() throws Exception {
        // given
        CartItemRequestDto requestDto = new CartItemRequestDto();
        requestDto.setProductId(product.getId());
        requestDto.setQuantity(2);
        cartService.addItemToCart(user.getEmail(), requestDto);

        // when
        MvcResult orderResult = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated())
                .andReturn();
        Long orderId = objectMapper.readValue(orderResult.getResponse().getContentAsString(), Long.class);

        // then
        Order createdOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);

        // when
        mockMvc.perform(post("/api/payments/internal/" + orderId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // then
        Order completedOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.ORDERED);

        User finalUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(finalUser.getPoints()).isEqualTo(100000 - 20000);

        Product finalProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(finalProduct.getStockQuantity()).isEqualTo(100 - 2);

        assertThat(paymentRepository.findAll()).hasSize(1);
        assertThat(cartRepository.findByUserId(user.getId()).get().getCartItems()).isEmpty();
    }

    @Test
    @DisplayName("실패 - 재고 부족")
    void order_fail_notEnoughStock() throws Exception {
        // given
        CartItemRequestDto requestDto = new CartItemRequestDto();
        requestDto.setProductId(product.getId());
        requestDto.setQuantity(200);
        cartService.addItemToCart(user.getEmail(), requestDto);

        // when & then
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isInternalServerError());
    }
}
