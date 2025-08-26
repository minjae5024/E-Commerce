package minjae5024.ECommerceProject.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateRequest {
    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    private String description;

    @NotBlank(message = "카테고리는 필수입니다.")
    private String category;

    private String brand;

    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Long price;

    @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    private Integer stock;

    @NotBlank(message = "상태는 필수입니다.")
    private String status;
}
