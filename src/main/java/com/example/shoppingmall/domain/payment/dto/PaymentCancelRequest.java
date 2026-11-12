package com.example.shoppingmall.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PaymentCancelRequest(

        @NotBlank(message = "취소 사유는 필수입니다.")
        @Size(max = 200, message = "취소 사유는 200자 이하여야 합니다.")
        String reason
) {
}