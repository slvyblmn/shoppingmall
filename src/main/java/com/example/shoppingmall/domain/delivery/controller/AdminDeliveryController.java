package com.example.shoppingmall.domain.delivery.controller;

import com.example.shoppingmall.domain.delivery.dto.DeliveryCreateRequest;
import com.example.shoppingmall.domain.delivery.dto.DeliveryResponse;
import com.example.shoppingmall.domain.delivery.dto.DeliveryUpdateRequest;
import com.example.shoppingmall.domain.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/deliveries")
@RequiredArgsConstructor
public class AdminDeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<DeliveryResponse> register(@Valid @RequestBody DeliveryCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.register(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DeliveryResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody DeliveryUpdateRequest request) {
        return ResponseEntity.ok(deliveryService.update(id, request));
    }
}