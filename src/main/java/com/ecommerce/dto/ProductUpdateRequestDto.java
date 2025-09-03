package com.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateRequestDto {

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
}
