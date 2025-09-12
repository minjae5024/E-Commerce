package com.ecommerce.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Builder
    public Payment(Order order, String paymentMethod, int amount) {
        this.order = order;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentDate = LocalDateTime.now();
    }
}
