package com.example.shoppingmall.domain.delivery.dto;

import com.example.shoppingmall.domain.delivery.entity.Delivery;
import com.example.shoppingmall.domain.delivery.entity.DeliveryStatus;
import com.example.shoppingmall.domain.order.entity.Order;
import com.example.shoppingmall.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;

public record DeliveryResponse(
        Long id,
        Long orderId,
        String orderNumber,
        OrderStatus orderStatus,
        DeliveryStatus status,
        String statusDescription,
        String courier,
        String trackingNumber,
        String receiverName,
        String receiverPhone,
        String address,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt
) {

    public static DeliveryResponse from(Delivery delivery) {
        Order order = delivery.getOrder();
        return new DeliveryResponse(
                delivery.getId(),
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                delivery.getStatus(),
                delivery.getStatus().getDescription(),
                delivery.getCourier(),
                delivery.getTrackingNumber(),
                order.getShippingInfo().getReceiverName(),
                order.getShippingInfo().getReceiverPhone(),
                order.getShippingInfo().getAddress(),
                delivery.getShippedAt(),
                delivery.getDeliveredAt()
        );
    }
}