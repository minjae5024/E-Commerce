package com.ecommerce.controller;

import com.ecommerce.dto.OrderDetailResponseDto;
import com.ecommerce.dto.OrderResponseDto;
import com.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;

@Tag(name = "주문 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성")
    @PostMapping
    public ResponseEntity<Long> createOrder(Principal principal) {
        Long orderId = orderService.createOrder(principal.getName());
        return ResponseEntity.created(URI.create("/api/orders/" + orderId)).body(orderId);
    }

    @Operation(summary = "주문 목록 조회")
    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getUserOrders(Principal principal, @ParameterObject Pageable pageable) {
        Page<OrderResponseDto> orders = orderService.findUserOrders(principal.getName(), pageable);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "주문 상세 조회")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponseDto> getOrderDetails(@PathVariable Long orderId, Principal principal) {
        OrderDetailResponseDto orderDetails = orderService.findOrderDetails(principal.getName(), orderId);
        return ResponseEntity.ok(orderDetails);
    }

    @Operation(summary = "주문 취소")
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Long> cancelOrder(@PathVariable Long orderId, Principal principal) {
        orderService.cancelOrder(principal.getName(), orderId);
        return ResponseEntity.ok(orderId);
    }
}
