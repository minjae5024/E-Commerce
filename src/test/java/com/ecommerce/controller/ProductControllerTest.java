package com.ecommerce.controller;

import com.ecommerce.config.jwt.JwtTokenProvider;
import com.ecommerce.domain.Product;
import com.ecommerce.domain.Role;
import com.ecommerce.domain.User;
import com.ecommerce.dto.ProductCreateRequestDto;
import com.ecommerce.dto.ProductUpdateRequestDto;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        Authentication adminAuth = new UsernamePasswordAuthenticationToken("testAdmin@test.com", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        adminToken = jwtTokenProvider.generateToken(adminAuth);

        Authentication userAuth = new UsernamePasswordAuthenticationToken("testUser@test.com", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        userToken = jwtTokenProvider.generateToken(userAuth);
    }

    @Test
    @DisplayName("관리자 권한으로 상품 등록 성공")
    void createProduct_byAdmin_success() throws Exception {
        // given
        ProductCreateRequestDto requestDto = new ProductCreateRequestDto();
        requestDto.setName("testProduct");
        requestDto.setPrice(10000);
        requestDto.setStockQuantity(100);
        requestDto.setDescription("testDescription");

        String requestBody = objectMapper.writeValueAsString(requestDto);

        // when & then
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("일반 사용자 권한으로 상품 등록 실패")
    void createProduct_byUser_fail() throws Exception {
        // given
        ProductCreateRequestDto requestDto = new ProductCreateRequestDto();
        requestDto.setName("testProduct");
        requestDto.setPrice(10000);
        requestDto.setStockQuantity(100);
        requestDto.setDescription("testDescription");

        String requestBody = objectMapper.writeValueAsString(requestDto);

        // when & then
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 상세 조회 성공")
    void getProductById_success() throws Exception {
        // given
        Product product = productRepository.save(Product.builder()
                .name("testProduct")
                .price(10000)
                .stockQuantity(100)
                .description("testDescription")
                .build());

        // when
        ResultActions result = mockMvc.perform(get("/api/products/" + product.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testProduct"))
                .andExpect(jsonPath("$.price").value(10000))
                .andExpect(jsonPath("$.stockQuantity").value(100))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 상품 상세 조회 실패")
    void getProductById_notFound() throws Exception {
        // given
        long nonExistentId = 999L;

        // when & then
        mockMvc.perform(get("/api/products/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 목록 페이징 조회 성공")
    void getAllProducts_paged_success() throws Exception {
        // given
        List<Product> products = IntStream.range(1, 21).mapToObj(i -> Product.builder()
                .name("testProduct " + i)
                .price(1000 * i)
                .stockQuantity(100)
                .description("testDescription " + i)
                .build()).collect(Collectors.toList());
        productRepository.saveAll(products);

        // when & then
        mockMvc.perform(get("/api/products?page=1&size=5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.totalPages").value(4))
                .andExpect(jsonPath("$.totalElements").value(20))
                .andDo(print());
    }

    @Test
    @DisplayName("관리자 권한으로 상품 수정 성공")
    void updateProduct_byAdmin_success() throws Exception {
        // given
        Product product = productRepository.save(Product.builder()
                .name("originalName")
                .price(10000)
                .stockQuantity(100)
                .description("originalDescription")
                .build());

        ProductUpdateRequestDto requestDto = new ProductUpdateRequestDto();
        requestDto.setName("updatedName");
        requestDto.setPrice(20000);
        requestDto.setStockQuantity(50);
        requestDto.setDescription("updatedDescription");

        // when
        mockMvc.perform(put("/api/products/" + product.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        // then
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getName()).isEqualTo("updatedName");
        assertThat(updatedProduct.getPrice()).isEqualTo(20000);
    }

    @Test
    @DisplayName("일반 사용자 권한으로 상품 수정 실패")
    void updateProduct_byUser_fail() throws Exception {
        // given
        Product product = productRepository.save(Product.builder().name("originalName").price(100).stockQuantity(10).description("desc").build());
        ProductUpdateRequestDto requestDto = new ProductUpdateRequestDto();
        requestDto.setName("updatedName");

        // when & then
        mockMvc.perform(put("/api/products/" + product.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 권한으로 상품 삭제 성공")
    void deleteProduct_byAdmin_success() throws Exception {
        // given
        Product product = productRepository.save(Product.builder().name("toBeDeleted").price(100).stockQuantity(10).description("desc").build());

        // when
        mockMvc.perform(delete("/api/products/" + product.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // then
        assertThat(productRepository.findById(product.getId())).isEmpty();
    }

    @Test
    @DisplayName("일반 사용자 권한으로 상품 삭제 실패")
    void deleteProduct_byUser_fail() throws Exception {
        // given
        Product product = productRepository.save(Product.builder().name("toBeDeleted").price(100).stockQuantity(10).description("desc").build());

        // when & then
        mockMvc.perform(delete("/api/products/" + product.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}
