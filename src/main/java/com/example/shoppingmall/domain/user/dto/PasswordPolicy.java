package com.example.shoppingmall.domain.user.dto;

final class PasswordPolicy {

    static final String REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,20}$";
    static final String MESSAGE = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~20자여야 합니다.";

    private PasswordPolicy() {
    }
}