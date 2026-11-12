package com.example.shoppingmall.domain.delivery.dto;

import com.example.shoppingmall.domain.delivery.entity.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DeliveryUpdateRequest(

        @NotNull(message = "배송 상태는 필수입니다.")
        DeliveryStatus status,

        @Size(max = 50, message = "택배사는 50자 이하여야 합니다.")
        String courier,

        @Size(max = 50, message = "운송장 번호는 50자 이하여야 합니다.")
        String trackingNumber
) {
}