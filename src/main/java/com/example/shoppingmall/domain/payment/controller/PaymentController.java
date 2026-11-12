package com.example.shoppingmall.domain.payment.controller;

import com.example.shoppingmall.domain.payment.dto.PaymentCancelRequest;
import com.example.shoppingmall.domain.payment.dto.PaymentConfirmRequest;
import com.example.shoppingmall.domain.payment.dto.PaymentPrepareRequest;
import com.example.shoppingmall.domain.payment.dto.PaymentPrepareResponse;
import com.example.shoppingmall.domain.payment.dto.PaymentResponse;
import com.example.shoppingmall.domain.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/prepare")
    public ResponseEntity<PaymentPrepareResponse> prepare(@AuthenticationPrincipal Long userId,
                                                          @Valid @RequestBody PaymentPrepareRequest request) {
        return ResponseEntity.ok(paymentService.prepare(userId, request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirm(@AuthenticationPrincipal Long userId,
                                                   @Valid @RequestBody PaymentConfirmRequest request) {
        return ResponseEntity.ok(paymentService.confirm(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> get(@AuthenticationPrincipal Long userId,
                                               @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.get(userId, id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PaymentResponse> cancel(@AuthenticationPrincipal Long userId,
                                                  @PathVariable Long id,
                                                  @Valid @RequestBody PaymentCancelRequest request) {
        return ResponseEntity.ok(paymentService.cancel(userId, id, request));
    }
}