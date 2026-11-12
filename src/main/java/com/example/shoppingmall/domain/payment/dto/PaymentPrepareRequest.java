package com.example.shoppingmall.domain.payment.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentPrepareRequest(

        @NotNull(message = "주문 ID는 필수입니다.")
        Long orderId
) {
}