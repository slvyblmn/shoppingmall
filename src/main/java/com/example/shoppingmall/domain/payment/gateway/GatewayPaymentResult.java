package com.example.shoppingmall.domain.payment.gateway;

public record GatewayPaymentResult(
        String paymentId,
        GatewayPaymentStatus status,
        long amount,
        String currency,
        String failReason
) {

    public boolean matches(int expectedAmount, String expectedCurrency) {
        return amount == expectedAmount && expectedCurrency.equals(currency);
    }
}