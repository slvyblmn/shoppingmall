package com.example.shoppingmall.domain.order.dto;

import com.example.shoppingmall.domain.order.entity.Order;
import com.example.shoppingmall.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long orderId,
        String orderNumber,
        OrderStatus status,
        String statusDescription,
        int totalPrice,
        String receiverName,
        String receiverPhone,
        String address,
        List<OrderItemResponse> items,
        LocalDateTime orderedAt,
        LocalDateTime cancelledAt
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getStatus().getDescription(),
                order.getTotalPrice(),
                order.getShippingInfo().getReceiverName(),
                order.getShippingInfo().getReceiverPhone(),
                order.getShippingInfo().getAddress(),
                order.getOrderItems().stream().map(OrderItemResponse::from).toList(),
                order.getCreatedAt(),
                order.getCancelledAt()
        );
    }
}