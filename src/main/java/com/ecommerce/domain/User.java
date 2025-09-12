package com.ecommerce.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private int points;

    @Builder
    public User(String email, String password, String name, String address, Role role, int points) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
        this.role = role;
        this.points = points;
    }

    public void usePoints(int amount) {
        if (this.points < amount) {
            throw new IllegalStateException("Not enough points");
        }
        this.points -= amount;
    }
}
