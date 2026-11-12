package com.example.shoppingmall.domain.order.entity;

import com.example.shoppingmall.domain.user.entity.User;
import com.example.shoppingmall.global.common.BaseTimeEntity;
import com.example.shoppingmall.global.exception.BusinessException;
import com.example.shoppingmall.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false)
    private int totalPrice;

    @Embedded
    private ShippingInfo shippingInfo;

    private LocalDateTime paidAt;

    private LocalDateTime cancelledAt;

    private LocalDateTime refundedAt;

    public static Order create(User user, ShippingInfo shippingInfo, List<OrderItem> items) {
        Order order = new Order();
        order.orderNumber = generateOrderNumber();
        order.user = user;
        order.shippingInfo = shippingInfo;
        order.status = OrderStatus.PENDING;
        items.forEach(order::addOrderItem);
        order.totalPrice = items.stream().mapToInt(OrderItem::calculateLinePrice).sum();
        return order;
    }

    private void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.assignOrder(this);
    }

    public boolean isOwnedBy(Long userId) {
        return user.getId().equals(userId);
    }

    public boolean isPending() {
        return status == OrderStatus.PENDING;
    }

    public boolean isRefundable() {
        return status == OrderStatus.PAID || status == OrderStatus.PREPARING;
    }

    public String summarizeItems() {
        if (orderItems.isEmpty()) {
            return "";
        }
        String firstName = orderItems.get(0).getProductName();
        return orderItems.size() == 1 ? firstName : firstName + " 외 " + (orderItems.size() - 1) + "건";
    }

    public void cancel() {
        if (status != OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
        }
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void markPaid() {
        if (status != OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.ORDER_NOT_PAYABLE);
        }
        this.status = OrderStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void refund() {
        if (!isRefundable()) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_CANCELLABLE);
        }
        this.status = OrderStatus.REFUNDED;
        this.refundedAt = LocalDateTime.now();
    }

    public void startPreparing() {
        if (status != OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.ORDER_NOT_DELIVERABLE);
        }
        this.status = OrderStatus.PREPARING;
    }

    public void startShipping() {
        if (status != OrderStatus.PREPARING) {
            throw new BusinessException(ErrorCode.INVALID_DELIVERY_STATUS);
        }
        this.status = OrderStatus.SHIPPING;
    }

    public void completeDelivery() {
        if (status != OrderStatus.SHIPPING) {
            throw new BusinessException(ErrorCode.INVALID_DELIVERY_STATUS);
        }
        this.status = OrderStatus.DELIVERED;
    }

    private static String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "ORD-" + date + "-" + random;
    }
}