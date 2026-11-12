package com.example.shoppingmall.domain.user.entity;

import com.example.shoppingmall.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean deleted;

    private LocalDateTime deletedAt;

    private User(String email, String password, String name, String phone, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.deleted = false;
    }

    public static User createUser(String email, String encodedPassword, String name, String phone) {
        return new User(email, encodedPassword, name, phone, Role.USER);
    }

    public static User createAdmin(String email, String encodedPassword, String name) {
        return new User(email, encodedPassword, name, null, Role.ADMIN);
    }

    public void updateProfile(String name, String phone) {
        if (StringUtils.hasText(name)) {
            this.name = name;
        }
        if (StringUtils.hasText(phone)) {
            this.phone = phone;
        }
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void withdraw() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.email = "withdrawn_" + this.id + "@deleted.local";
        this.name = "탈퇴회원";
        this.phone = null;
    }
}