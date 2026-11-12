package com.example.shoppingmall.domain.cart.controller;

import com.example.shoppingmall.domain.cart.dto.CartItemAddRequest;
import com.example.shoppingmall.domain.cart.dto.CartItemUpdateRequest;
import com.example.shoppingmall.domain.cart.dto.CartResponse;
import com.example.shoppingmall.domain.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@AuthenticationPrincipal Long userId,
                                                @Valid @RequestBody CartItemAddRequest request) {
        return ResponseEntity.ok(cartService.addItem(userId, request));
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PatchMapping("/items/{id}")
    public ResponseEntity<CartResponse> updateItemQuantity(@AuthenticationPrincipal Long userId,
                                                           @PathVariable Long id,
                                                           @Valid @RequestBody CartItemUpdateRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, id, request));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> removeItem(@AuthenticationPrincipal Long userId,
                                           @PathVariable Long id) {
        cartService.removeItem(userId, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@AuthenticationPrincipal Long userId) {
        cartService.clear(userId);
        return ResponseEntity.noContent().build();
    }
}