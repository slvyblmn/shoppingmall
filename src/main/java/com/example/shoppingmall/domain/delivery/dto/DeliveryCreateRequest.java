package com.example.shoppingmall.domain.delivery.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DeliveryCreateRequest(

        @NotNull(message = "주문 ID는 필수입니다.")
        Long orderId,

        @Size(max = 50, message = "택배사는 50자 이하여야 합니다.")
        String courier,

        @Size(max = 50, message = "운송장 번호는 50자 이하여야 합니다.")
        String trackingNumber
) {
}