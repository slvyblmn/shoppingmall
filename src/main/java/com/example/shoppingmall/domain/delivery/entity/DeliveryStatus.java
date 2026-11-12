package com.example.shoppingmall.domain.delivery.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryStatus {

    PREPARING("상품 준비 중"),
    SHIPPING("배송 중"),
    DELIVERED("배송 완료");

    private final String description;

    public boolean canTransitionTo(DeliveryStatus next) {
        return (this == PREPARING && next == SHIPPING)
                || (this == SHIPPING && next == DELIVERED);
    }
}