package com.example.shoppingmall.domain.payment.entity;

import com.example.shoppingmall.domain.order.entity.Order;
import com.example.shoppingmall.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

    private static final int REASON_MAX_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false, unique = true, length = 80)
    private String paymentId;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(length = REASON_MAX_LENGTH)
    private String failReason;

    @Column(length = REASON_MAX_LENGTH)
    private String cancelReason;

    private LocalDateTime paidAt;

    private LocalDateTime cancelledAt;

    public static Payment create(Order order) {
        Payment payment = new Payment();
        payment.order = order;
        payment.paymentId = generatePaymentId(order);
        payment.amount = order.getTotalPrice();
        payment.status = PaymentStatus.READY;
        return payment;
    }

    public boolean isOwnedBy(Long userId) {
        return order.isOwnedBy(userId);
    }

    public void resetForRetry() {
        if (status != PaymentStatus.FAILED) {
            return;
        }
        this.paymentId = generatePaymentId(order);
        this.status = PaymentStatus.READY;
        this.failReason = null;
    }

    public void markPaid() {
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
        this.failReason = null;
    }

    public void markFailed(String reason) {
        if (status == PaymentStatus.PAID || status == PaymentStatus.CANCELLED) {
            return;
        }
        this.status = PaymentStatus.FAILED;
        this.failReason = truncate(reason);
    }

    public void cancel(String reason) {
        this.status = PaymentStatus.CANCELLED;
        this.cancelReason = truncate(reason);
        this.cancelledAt = LocalDateTime.now();
    }

    private static String generatePaymentId(Order order) {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "PAY-" + order.getOrderNumber() + "-" + suffix;
    }

    private static String truncate(String value) {
        if (value == null) {
            return null;
        }
        return value.length() > REASON_MAX_LENGTH ? value.substring(0, REASON_MAX_LENGTH) : value;
    }
}