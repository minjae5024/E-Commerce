package com.ecommerce.controller;

import com.ecommerce.config.jwt.JwtTokenProvider;
import com.ecommerce.domain.*;
import com.ecommerce.dto.CartItemRequestDto;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.CartService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CartControllerTest {

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
    private CartService cartService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String userToken;
    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        productRepository.deleteAll();
        cartRepository.deleteAll();
        cartItemRepository.deleteAll();

        user = User.builder()
                .email("testUser@test.com")
                .password(passwordEncoder.encode("testPassword"))
                .name("testUser")
                .role(Role.USER)
                .points(1000000)
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
    @DisplayName("장바구니에 상품 추가 성공")
    void addItemToCart_success() throws Exception {
        // given
        CartItemRequestDto requestDto = new CartItemRequestDto();
        requestDto.setProductId(product.getId());
        requestDto.setQuantity(2);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        // when & then
        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 조회 성공")
    void getCart_success() throws Exception {
        // given
        CartItemRequestDto requestDto = new CartItemRequestDto();
        requestDto.setProductId(product.getId());
        requestDto.setQuantity(2);
        cartService.addItemToCart(user.getEmail(), requestDto);

        // when & then
        mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems[0].productName").value("testProduct"))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(20000))
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 상품 수량 변경 성공")
    void updateCartItemQuantity_success() throws Exception {
        // given
        CartItemRequestDto requestDto = new CartItemRequestDto();
        requestDto.setProductId(product.getId());
        requestDto.setQuantity(2);
        Long cartItemId = cartService.addItemToCart(user.getEmail(), requestDto);

        Map<String, Integer> updateRequest = Map.of("quantity", 5);
        String requestBody = objectMapper.writeValueAsString(updateRequest);

        // when & then
        mockMvc.perform(patch("/api/cart/items/" + cartItemId)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        CartItem updatedItem = cartItemRepository.findById(cartItemId).orElseThrow();
        assertThat(updatedItem.getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("장바구니 상품 삭제 성공")
    void removeCartItem_success() throws Exception {
        // given
        CartItemRequestDto requestDto = new CartItemRequestDto();
        requestDto.setProductId(product.getId());
        requestDto.setQuantity(2);
        Long cartItemId = cartService.addItemToCart(user.getEmail(), requestDto);

        // when & then
        mockMvc.perform(delete("/api/cart/items/" + cartItemId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());

        assertThat(cartItemRepository.findById(cartItemId)).isEmpty();
    }
}
