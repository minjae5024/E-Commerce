package com.ecommerce.controller;

import com.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/internal/{orderId}")
    public ResponseEntity<Long> processInternalPayment(@PathVariable Long orderId, Principal principal) {
        Long paymentId = paymentService.processInternalPayment(principal.getName(), orderId);
        return ResponseEntity.ok(paymentId);
    }
}
