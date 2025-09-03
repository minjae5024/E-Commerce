package com.ecommerce.dto;

import com.ecommerce.domain.Product;
import lombok.Getter;

@Getter
public class ProductResponseDto {
    private final Long id;
    private final String name;
    private final Integer price;
    private final Integer stockQuantity;
    private final String description;

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.stockQuantity = product.getStockQuantity();
        this.description = product.getDescription();
    }
}
