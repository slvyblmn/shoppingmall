package com.example.shoppingmall.domain.payment.dto;

import com.example.shoppingmall.domain.order.entity.Order;
import com.example.shoppingmall.domain.order.entity.OrderStatus;
import com.example.shoppingmall.domain.payment.entity.Payment;
import com.example.shoppingmall.domain.payment.entity.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        String paymentId,
        Long orderId,
        String orderNumber,
        OrderStatus orderStatus,
        int amount,
        PaymentStatus status,
        String failReason,
        String cancelReason,
        LocalDateTime paidAt,
        LocalDateTime cancelledAt
) {

    public static PaymentResponse from(Payment payment) {
        Order order = payment.getOrder();
        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentId(),
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getFailReason(),
                payment.getCancelReason(),
                payment.getPaidAt(),
                payment.getCancelledAt()
        );
    }
}