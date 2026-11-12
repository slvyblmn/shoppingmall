package com.example.shoppingmall.domain.user.dto;

final class PhonePolicy {

    static final String REGEX = "^01[016789]-?\\d{3,4}-?\\d{4}$";
    static final String MESSAGE = "올바른 휴대폰 번호 형식이 아닙니다.";

    private PhonePolicy() {
    }
}