package com.example.shoppingmall.domain.order.controller;

import com.example.shoppingmall.domain.order.dto.OrderCreateRequest;
import com.example.shoppingmall.domain.order.dto.OrderResponse;
import com.example.shoppingmall.domain.order.dto.OrderSummaryResponse;
import com.example.shoppingmall.domain.order.service.OrderService;
import com.example.shoppingmall.global.common.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@AuthenticationPrincipal Long userId,
                                                @Valid @RequestBody OrderCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(userId, request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderSummaryResponse>> getMyOrders(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.getMyOrders(userId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getMyOrder(@AuthenticationPrincipal Long userId,
                                                    @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getMyOrder(userId, id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@AuthenticationPrincipal Long userId,
                                                @PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancel(userId, id));
    }
}