package com.example.shoppingmall.domain.cart.dto;

import com.example.shoppingmall.domain.cart.entity.CartItem;
import com.example.shoppingmall.domain.product.entity.Product;

public record CartItemResponse(
        Long cartItemId,
        Long productId,
        String productName,
        int price,
        int quantity,
        int linePrice,
        int stock,
        boolean available
) {

    public static CartItemResponse from(CartItem item) {
        Product product = item.getProduct();
        boolean available = product.isPurchasable() && item.getQuantity() <= product.getStock();

        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                item.getQuantity(),
                item.calculateLinePrice(),
                product.getStock(),
                available
        );
    }
}