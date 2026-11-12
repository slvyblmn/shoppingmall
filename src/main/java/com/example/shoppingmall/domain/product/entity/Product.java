package com.example.shoppingmall.domain.product.entity;

import com.example.shoppingmall.domain.category.entity.Category;
import com.example.shoppingmall.global.common.BaseTimeEntity;
import com.example.shoppingmall.global.exception.BusinessException;
import com.example.shoppingmall.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @Column(nullable = false)
    private boolean deleted;

    private LocalDateTime deletedAt;

    private Product(Category category, String name, String description, int price, int stock) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.status = ProductStatus.ON_SALE;
        this.deleted = false;
    }

    public static Product create(Category category, String name, String description, int price, int stock) {
        return new Product(category, name, description, price, stock);
    }

    public void update(Category category, String name, String description,
                       Integer price, Integer stock, ProductStatus status) {
        if (category != null) {
            this.category = category;
        }
        if (StringUtils.hasText(name)) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (price != null) {
            this.price = price;
        }
        if (stock != null) {
            this.stock = stock;
        }
        if (status != null) {
            this.status = status;
        }
    }

    public boolean isSoldOut() {
        return stock == 0;
    }

    public boolean isPurchasable() {
        return !deleted && status == ProductStatus.ON_SALE && stock > 0;
    }

    public void decreaseStock(int quantity) {
        if (stock < quantity) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }
        this.stock -= quantity;
    }

    public void increaseStock(int quantity) {
        this.stock += quantity;
    }

    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.status = ProductStatus.STOPPED;
    }
}