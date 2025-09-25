package com.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequestDto {
    @NotNull(message = "상품 ID는 필수 항목입니다.")
    private Long productId;

    @NotNull(message = "수량은 필수 항목입니다.")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    private int quantity;
}
