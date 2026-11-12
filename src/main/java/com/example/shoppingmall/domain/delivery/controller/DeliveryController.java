package com.example.shoppingmall.domain.delivery.controller;

import com.example.shoppingmall.domain.delivery.dto.DeliveryResponse;
import com.example.shoppingmall.domain.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/api/orders/{orderId}/delivery")
    public ResponseEntity<DeliveryResponse> getByOrder(@AuthenticationPrincipal Long userId,
                                                       @PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.getByOrder(userId, orderId));
    }
}