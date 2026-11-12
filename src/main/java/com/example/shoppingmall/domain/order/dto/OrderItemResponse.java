package com.example.shoppingmall.domain.order.dto;

import com.example.shoppingmall.domain.order.entity.OrderItem;

public record OrderItemResponse(
        Long orderItemId,
        Long productId,
        String productName,
        int orderPrice,
        int quantity,
        int linePrice
) {

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProductName(),
                item.getOrderPrice(),
                item.getQuantity(),
                item.calculateLinePrice()
        );
    }
}