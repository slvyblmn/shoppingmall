package com.example.shoppingmall.domain.delivery.entity;

import com.example.shoppingmall.domain.order.entity.Order;
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
@Table(name = "deliveries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryStatus status;

    @Column(length = 50)
    private String courier;

    @Column(length = 50)
    private String trackingNumber;

    private LocalDateTime shippedAt;

    private LocalDateTime deliveredAt;

    public static Delivery start(Order order, String courier, String trackingNumber) {
        order.startPreparing();

        Delivery delivery = new Delivery();
        delivery.order = order;
        delivery.status = DeliveryStatus.PREPARING;
        delivery.updateTrackingInfo(courier, trackingNumber);
        return delivery;
    }

    public void updateTrackingInfo(String courier, String trackingNumber) {
        if (StringUtils.hasText(courier)) {
            this.courier = courier;
        }
        if (StringUtils.hasText(trackingNumber)) {
            this.trackingNumber = trackingNumber;
        }
    }

    public void changeStatus(DeliveryStatus next) {
        if (next == status) {
            if (status == DeliveryStatus.DELIVERED) {
                throw new BusinessException(ErrorCode.INVALID_DELIVERY_STATUS);
            }
            return;
        }
        if (!status.canTransitionTo(next)) {
            throw new BusinessException(ErrorCode.INVALID_DELIVERY_STATUS);
        }

        if (next == DeliveryStatus.SHIPPING) {
            if (!hasTrackingInfo()) {
                throw new BusinessException(ErrorCode.TRACKING_INFO_REQUIRED);
            }
            order.startShipping();
            this.shippedAt = LocalDateTime.now();
        } else if (next == DeliveryStatus.DELIVERED) {
            order.completeDelivery();
            this.deliveredAt = LocalDateTime.now();
        }

        this.status = next;
    }

    public boolean hasTrackingInfo() {
        return StringUtils.hasText(courier) && StringUtils.hasText(trackingNumber);
    }
}