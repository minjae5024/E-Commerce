package minjae5024.ECommerceProject.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String brand;
    private Long price;
    private Integer stock;
    private String status;
}