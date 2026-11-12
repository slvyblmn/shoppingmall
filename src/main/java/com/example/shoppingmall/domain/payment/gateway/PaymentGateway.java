package com.example.shoppingmall.domain.payment.gateway;

public interface PaymentGateway {

    GatewayPaymentResult getPayment(String paymentId);

    void cancelPayment(String paymentId, String reason);
}