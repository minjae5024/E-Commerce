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

    @NotBlank(message = "상품명은 필수 항목입니다.")
    private String name;

    @NotNull(message = "가격은 필수 항목입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "재고 수량은 필수 항목입니다.")
    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다.")
    private Integer stockQuantity;

    @NotBlank(message = "상품 설명은 필수 항목입니다.")
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
