package com.ecommerce.dto;

import com.ecommerce.domain.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateRequestDto {

    @NotBlank(message = "Product name is required.")
    private String name;

    @NotNull(message = "Price is required.")
    @Min(value = 0, message = "Price must be non-negative.")
    private Integer price;

    @NotNull(message = "Stock quantity is required.")
    @Min(value = 0, message = "Stock quantity must be non-negative.")
    private Integer stockQuantity;

    @NotBlank(message = "Description is required.")
    private String description;

    public Product toEntity() {
        return Product.builder()
                .name(this.name)
                .price(this.price)
                .stockQuantity(this.stockQuantity)
                .description(this.description)
                .build();
    }
}
