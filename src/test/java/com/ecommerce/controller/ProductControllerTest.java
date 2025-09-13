package com.ecommerce.controller;

import com.ecommerce.config.jwt.JwtTokenProvider;
import com.ecommerce.domain.Product;
import com.ecommerce.domain.Role;
import com.ecommerce.domain.User;
import com.ecommerce.dto.ProductCreateRequestDto;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        productRepository.deleteAll();

        User admin = User.builder()
                .email("testAdmin@test.com")
                .password(passwordEncoder.encode("testPassword"))
                .name("testAdmin")
                .role(Role.ADMIN)
                .points(0)
                .build();
        userRepository.save(admin);

        User user = User.builder()
                .email("testUser@test.com")
                .password(passwordEncoder.encode("testPassword"))
                .name("testUser")
                .role(Role.USER)
                .points(1000000)
                .build();
        userRepository.save(user);

        Authentication adminAuth = new UsernamePasswordAuthenticationToken(admin.getEmail(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        adminToken = jwtTokenProvider.generateToken(adminAuth);

        Authentication userAuth = new UsernamePasswordAuthenticationToken(user.getEmail(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        userToken = jwtTokenProvider.generateToken(userAuth);
    }

    @Test
    @DisplayName("상품 등록 API 성공 - 관리자")
    void createProduct_success_asAdmin() throws Exception {
        // given
        ProductCreateRequestDto requestDto = new ProductCreateRequestDto();
        requestDto.setName("testProduct");
        requestDto.setPrice(10000);
        requestDto.setStockQuantity(100);
        requestDto.setDescription("testDescription");

        // when & then
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 등록 API 실패 - 일반 사용자")
    void createProduct_fail_asUser() throws Exception {
        // given
        ProductCreateRequestDto requestDto = new ProductCreateRequestDto();
        requestDto.setName("testProduct");
        requestDto.setPrice(10000);
        requestDto.setStockQuantity(100);
        requestDto.setDescription("testDescription");

        // when & then
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 상세 조회 API 성공")
    void getProduct_success() throws Exception {
        // given
        Product product = Product.builder()
                .name("testProduct")
                .price(10000)
                .stockQuantity(100)
                .description("testDescription")
                .build();
        productRepository.save(product);

        // when & then
        mockMvc.perform(get("/api/products/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testProduct"))
                .andDo(print());
    }

    @Test
    @DisplayName("상품 상세 조회 API 실패 - 존재하지 않는 상품")
    void getProduct_fail_notFound() throws Exception {
        // given
        Long nonExistentProductId = 999L;

        // when & then
        mockMvc.perform(get("/api/products/" + nonExistentProductId))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
