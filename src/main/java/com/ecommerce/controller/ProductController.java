package com.ecommerce.controller;

import com.ecommerce.dto.ProductCreateRequestDto;
import com.ecommerce.dto.ProductResponseDto;
import com.ecommerce.dto.ProductUpdateRequestDto;
import com.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Parameter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Long> createProduct(@Valid @RequestBody ProductCreateRequestDto requestDto) {
        Long productId = productService.createProduct(requestDto);
        return ResponseEntity.created(URI.create("/api/products/" + productId)).body(productId);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long productId) {
        ProductResponseDto product = productService.findProductById(productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(@ParameterObject Pageable pageable) {
        Page<ProductResponseDto> products = productService.findAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Long> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductUpdateRequestDto requestDto) {
        Long updatedProductId = productService.updateProduct(productId, requestDto);
        return ResponseEntity.ok(updatedProductId);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}