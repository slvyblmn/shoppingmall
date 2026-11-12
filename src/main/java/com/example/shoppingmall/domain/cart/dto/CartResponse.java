package com.example.shoppingmall.domain.cart.dto;

import com.example.shoppingmall.domain.cart.entity.Cart;

import java.util.List;

public record CartResponse(
        Long cartId,
        List<CartItemResponse> items,
        int totalQuantity,
        int totalPrice
) {

    public static CartResponse from(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(CartItemResponse::from)
                .toList();

        int totalQuantity = items.stream().mapToInt(CartItemResponse::quantity).sum();
        int totalPrice = items.stream().mapToInt(CartItemResponse::linePrice).sum();

        return new CartResponse(cart.getId(), items, totalQuantity, totalPrice);
    }

    public static CartResponse empty() {
        return new CartResponse(null, List.of(), 0, 0);
    }
}