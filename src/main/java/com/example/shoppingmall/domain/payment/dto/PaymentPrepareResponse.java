package com.example.shoppingmall.domain.payment.dto;

public record PaymentPrepareResponse(
        Long id,
        String paymentId,
        String orderNumber,
        String orderName,
        int amount,
        String currency
) {
}