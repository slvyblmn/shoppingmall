package com.example.shoppingmall.domain.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductCreateRequest(

        @NotNull(message = "카테고리는 필수입니다.")
        Long categoryId,

        @NotBlank(message = "상품명은 필수입니다.")
        @Size(max = 100, message = "상품명은 100자 이하여야 합니다.")
        String name,

        @Size(max = 2000, message = "상품 설명은 2000자 이하여야 합니다.")
        String description,

        @NotNull(message = "가격은 필수입니다.")
        @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
        Integer price,

        @NotNull(message = "재고는 필수입니다.")
        @Min(value = 0, message = "재고는 0개 이상이어야 합니다.")
        Integer stock
) {
}