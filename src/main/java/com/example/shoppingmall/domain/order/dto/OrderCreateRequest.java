package com.example.shoppingmall.domain.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrderCreateRequest(

        @NotEmpty(message = "주문할 상품이 없습니다.")
        List<@Valid OrderItemRequest> items,

        @NotBlank(message = "받는 사람 이름은 필수입니다.")
        @Size(max = 50, message = "받는 사람 이름은 50자 이하여야 합니다.")
        String receiverName,

        @NotBlank(message = "받는 사람 연락처는 필수입니다.")
        @Pattern(regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다.")
        String receiverPhone,

        @NotBlank(message = "배송 주소는 필수입니다.")
        @Size(max = 255, message = "배송 주소는 255자 이하여야 합니다.")
        String address
) {
}