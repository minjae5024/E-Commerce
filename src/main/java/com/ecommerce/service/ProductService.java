package com.ecommerce.service;

import com.ecommerce.domain.Product;
import com.ecommerce.dto.ProductCreateRequestDto;
import com.ecommerce.dto.ProductResponseDto;
import com.ecommerce.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Long createProduct(ProductCreateRequestDto requestDto) {
        Product product = requestDto.toEntity();
        Product savedProduct = productRepository.save(product);
        return savedProduct.getId();
    }

    public ProductResponseDto findProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        return new ProductResponseDto(product);
    }

    public Page<ProductResponseDto> findAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(ProductResponseDto::new);
    }

    @Transactional
    public Long updateProduct(Long productId, com.ecommerce.dto.ProductUpdateRequestDto requestDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
        product.update(requestDto.getName(), requestDto.getPrice(), requestDto.getStockQuantity(), requestDto.getDescription());
        return productId;
    }

    @Transactional
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }
}
