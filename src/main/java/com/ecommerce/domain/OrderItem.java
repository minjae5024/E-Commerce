package com.ecommerce.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @Setter // For convenience method in Order
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int orderPrice; // Snapshot of price at time of order
    private int quantity;

    //== 생성 메소드 ==//
    public static OrderItem createOrderItem(Product product, int quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.product = product;
        orderItem.orderPrice = product.getPrice();
        orderItem.quantity = quantity;

        product.removeStock(quantity); // 재고 감소 로직 추가
        return orderItem;
    }

    //== 비즈니스 로직 ==//
    public int getTotalPrice() {
        return getOrderPrice() * getQuantity();
    }

    public void cancel() {
        getProduct().addStock(quantity);
    }
}
