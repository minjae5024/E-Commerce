package com.ecommerce.controller;

import com.ecommerce.dto.OrderDetailResponseDto;
import com.ecommerce.dto.OrderResponseDto;
import com.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> createOrder(Principal principal) {
        Long orderId = orderService.createOrder(principal.getName());
        return ResponseEntity.created(URI.create("/api/orders/" + orderId)).body(orderId);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getUserOrders(Principal principal, @ParameterObject Pageable pageable) {
        Page<OrderResponseDto> orders = orderService.findUserOrders(principal.getName(), pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponseDto> getOrderDetails(@PathVariable Long orderId, Principal principal) {
        OrderDetailResponseDto orderDetails = orderService.findOrderDetails(principal.getName(), orderId);
        return ResponseEntity.ok(orderDetails);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Long> cancelOrder(@PathVariable Long orderId, Principal principal) {
        orderService.cancelOrder(principal.getName(), orderId);
        return ResponseEntity.ok(orderId);
    }
}
