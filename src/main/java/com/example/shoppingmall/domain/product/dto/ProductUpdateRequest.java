package com.example.shoppingmall.domain.product.dto;

import com.example.shoppingmall.domain.product.entity.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ProductUpdateRequest(

        Long categoryId,

        @Size(max = 100, message = "상품명은 100자 이하여야 합니다.")
        String name,

        @Size(max = 2000, message = "상품 설명은 2000자 이하여야 합니다.")
        String description,

        @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
        Integer price,

        @Min(value = 0, message = "재고는 0개 이상이어야 합니다.")
        Integer stock,

        ProductStatus status
) {
}