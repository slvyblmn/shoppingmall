package com.example.shoppingmall.domain.review.entity;

import com.example.shoppingmall.domain.order.entity.OrderItem;
import com.example.shoppingmall.domain.product.entity.Product;
import com.example.shoppingmall.domain.user.entity.User;
import com.example.shoppingmall.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@Entity
@Table(
        name = "reviews",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reviews_order_item",
                columnNames = "order_item_id"
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = 1000)
    private String content;

    private Review(User user, Product product, OrderItem orderItem, int rating, String content) {
        this.user = user;
        this.product = product;
        this.orderItem = orderItem;
        this.rating = rating;
        this.content = content;
    }

    public static Review create(User user, Product product, OrderItem orderItem, int rating, String content) {
        return new Review(user, product, orderItem, rating, content);
    }

    public boolean isWrittenBy(Long userId) {
        return user.getId().equals(userId);
    }

    public void update(Integer rating, String content) {
        if (rating != null) {
            this.rating = rating;
        }
        if (StringUtils.hasText(content)) {
            this.content = content;
        }
    }
}