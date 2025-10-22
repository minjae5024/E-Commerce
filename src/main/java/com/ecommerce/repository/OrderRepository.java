package com.ecommerce.repository;

import com.ecommerce.domain.Order;
import com.ecommerce.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    Page<Order> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"orderItems", "user"})
    Optional<Order> findById(Long id);
}
