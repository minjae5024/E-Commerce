package com.ecommerce.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private int totalPrice;

    //== 연관관계 편의 메소드 ==//
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    //== 생성 메소드 ==//
    public static Order createOrder(User user, List<OrderItem> orderItems) {
        Order order = new Order();
        order.user = user;
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.status = OrderStatus.ORDERED;
        order.orderDate = LocalDateTime.now();
        order.totalPrice = order.calculateTotalPrice();
        return order;
    }

    //== 비즈니스 로직 ==//
    public void cancel() {
        this.status = OrderStatus.CANCELED;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    private int calculateTotalPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }
}
