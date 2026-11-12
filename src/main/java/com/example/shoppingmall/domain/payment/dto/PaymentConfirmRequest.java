package com.example.shoppingmall.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentConfirmRequest(

        @NotBlank(message = "결제 ID는 필수입니다.")
        String paymentId
) {
}