package com.example.shoppingmall.domain.order.dto;

import com.example.shoppingmall.domain.order.entity.Order;
import com.example.shoppingmall.domain.order.entity.OrderItem;
import com.example.shoppingmall.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderSummaryResponse(
        Long orderId,
        String orderNumber,
        OrderStatus status,
        String statusDescription,
        int totalPrice,
        String itemSummary,
        LocalDateTime orderedAt
) {

    public static OrderSummaryResponse from(Order order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getStatus().getDescription(),
                order.getTotalPrice(),
                summarize(order.getOrderItems()),
                order.getCreatedAt()
        );
    }

    private static String summarize(List<OrderItem> items) {
        if (items.isEmpty()) {
            return "";
        }
        String firstName = items.get(0).getProductName();
        return items.size() == 1 ? firstName : firstName + " 외 " + (items.size() - 1) + "건";
    }
}