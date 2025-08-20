package minjae5024.ECommerceProject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "products")
public class Product extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 200, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank
    @Column(length = 100, nullable = false)
    private String category;

    @Column(length = 100)
    private String brand;

    @NotNull @Min(0)
    private Long price;

    @NotNull @Min(0)
    private Integer stock;

    @NotNull
    @Column(length = 32, nullable = false)
    private String status; // enum 문자열 저장

    protected Product() {}
    public Product(String name, String description, String category, String brand,
                   Long price, Integer stock, String status) {
        this.name = name; this.description = description; this.category = category;
        this.brand = brand; this.price = price; this.stock = stock; this.status = status;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getBrand() { return brand; }
    public Long getPrice() { return price; }
    public Integer getStock() { return stock; }
    public String getStatus() { return status; }
}