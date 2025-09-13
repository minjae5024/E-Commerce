package com.ecommerce.service;

import com.ecommerce.domain.Product;
import com.ecommerce.dto.ProductCreateRequestDto;
import com.ecommerce.dto.ProductUpdateRequestDto;
import com.ecommerce.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 생성 성공")
    void createProduct_success() {
        // given
        ProductCreateRequestDto requestDto = new ProductCreateRequestDto();
        requestDto.setName("testName");
        requestDto.setPrice(10000);
        requestDto.setStockQuantity(100);
        requestDto.setDescription("testDescription");

        Product product = requestDto.toEntity();
        given(productRepository.save(any(Product.class))).willReturn(product);

        // when
        productService.createProduct(requestDto);

        // then
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("상품 ID로 조회 성공")
    void findProductById_success() {
        // given
        Long productId = 1L;
        Product product = Product.builder()
                .name("testName")
                .price(10000)
                .stockQuantity(100)
                .description("testDescription")
                .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        productService.findProductById(productId);

        // then
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID로 조회 시 예외 발생")
    void findProductById_fail_notfound() {
        // given
        Long productId = 1L;
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.findProductById(productId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("상품 정보 수정 성공")
    void updateProduct_success() {
        // given
        Long productId = 1L;
        Product product = Product.builder()
                .name("testName")
                .price(10000)
                .stockQuantity(100)
                .description("testDescription")
                .build();

        ProductUpdateRequestDto requestDto = new ProductUpdateRequestDto();
        requestDto.setName("updatedName");
        requestDto.setPrice(20000);
        requestDto.setStockQuantity(50);
        requestDto.setDescription("updatedDescription");

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        productService.updateProduct(productId, requestDto);

        // then
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void deleteProduct_success() {
        // given
        Long productId = 1L;

        // when
        productService.deleteProduct(productId);

        // then
        verify(productRepository).deleteById(productId);
    }
}
