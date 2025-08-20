package minjae5024.ECommerceProject.DTO;

import minjae5024.ECommerceProject.entity.Product;

public record ProductResponse(
        Long id, String name, String description,
        String category, String brand, Long price,
        Integer stock, String status
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(
                p.getId(), p.getName(), p.getDescription(),
                p.getCategory(), p.getBrand(), p.getPrice(),
                p.getStock(), p.getStatus()
        );
    }
}