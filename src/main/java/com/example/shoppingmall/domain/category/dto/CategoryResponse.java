package com.example.shoppingmall.domain.category.dto;

import com.example.shoppingmall.domain.category.entity.Category;

public record CategoryResponse(
        Long id,
        String name
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}