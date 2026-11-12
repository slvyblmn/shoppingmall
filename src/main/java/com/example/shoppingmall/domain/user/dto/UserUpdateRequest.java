package com.example.shoppingmall.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

        @Size(max = 50, message = "이름은 50자 이하여야 합니다.")
        String name,

        @Pattern(regexp = PhonePolicy.REGEX, message = PhonePolicy.MESSAGE)
        String phone,

        String currentPassword,

        @Pattern(regexp = PasswordPolicy.REGEX, message = PasswordPolicy.MESSAGE)
        String newPassword
) {
}