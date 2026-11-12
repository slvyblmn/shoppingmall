package com.example.shoppingmall.domain.user.dto;

public record TokenResponse(
        String grantType,
        String accessToken,
        long expiresInMs
) {

    public static TokenResponse bearer(String accessToken, long expiresInMs) {
        return new TokenResponse("Bearer", accessToken, expiresInMs);
    }
}