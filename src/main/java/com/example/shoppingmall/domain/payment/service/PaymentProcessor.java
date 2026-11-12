package com.example.shoppingmall.domain.payment.service;

import com.example.shoppingmall.domain.cart.repository.CartRepository;
import com.example.shoppingmall.domain.order.entity.Order;
import com.example.shoppingmall.domain.order.entity.OrderItem;
import com.example.shoppingmall.domain.payment.dto.PaymentResponse;
import com.example.shoppingmall.domain.payment.entity.Payment;
import com.example.shoppingmall.domain.payment.entity.PaymentStatus;
import com.example.shoppingmall.domain.payment.repository.PaymentRepository;
import com.example.shoppingmall.domain.product.entity.Product;
import com.example.shoppingmall.domain.product.entity.ProductStatus;
import com.example.shoppingmall.domain.product.repository.ProductRepository;
import com.example.shoppingmall.global.exception.BusinessException;
import com.example.shoppingmall.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentProcessor {

    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    @Transactional(readOnly = true)
    public Payment getOwnedPayment(Long userId, String paymentId) {
        return paymentRepository.findWithOrderByPaymentId(paymentId)
                .filter(payment -> payment.isOwnedBy(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Payment getOwnedPaymentById(Long userId, Long id) {
        return paymentRepository.findWithOrderById(id)
                .filter(payment -> payment.isOwnedBy(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    @Transactional
    public PaymentResponse complete(String paymentId) {
        Payment payment = paymentRepository.findByPaymentIdForUpdate(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() == PaymentStatus.PAID) {
            return PaymentResponse.from(payment);
        }
        if (payment.getStatus() != PaymentStatus.READY) {
            throw new BusinessException(ErrorCode.ORDER_NOT_PAYABLE);
        }

        Order order = payment.getOrder();
        if (!order.isPending()) {
            throw new BusinessException(ErrorCode.ORDER_NOT_PAYABLE);
        }

        Map<Long, Integer> quantities = collectQuantities(order);
        List<Product> products = productRepository.findAllByIdInForUpdate(quantities.keySet());
        if (products.size() != quantities.size()) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_ON_SALE);
        }

        for (Product product : products) {
            if (product.isDeleted() || product.getStatus() != ProductStatus.ON_SALE) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_ON_SALE);
            }
            product.decreaseStock(quantities.get(product.getId()));
        }

        payment.markPaid();
        order.markPaid();
        removeFromCart(order.getUser().getId(), quantities.keySet());

        return PaymentResponse.from(payment);
    }

    @Transactional
    public void markFailed(String paymentId, String reason) {
        paymentRepository.findByPaymentIdForUpdate(paymentId)
                .ifPresent(payment -> payment.markFailed(reason));
    }

    @Transactional
    public PaymentResponse completeRefund(Long id, String reason) {
        Payment payment = paymentRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_CANCELLABLE);
        }

        Order order = payment.getOrder();
        order.refund();

        Map<Long, Integer> quantities = collectQuantities(order);
        productRepository.findAllByIdInForUpdate(quantities.keySet())
                .forEach(product -> product.increaseStock(quantities.get(product.getId())));

        payment.cancel(reason);
        return PaymentResponse.from(payment);
    }

    private Map<Long, Integer> collectQuantities(Order order) {
        return order.getOrderItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getProduct().getId(),
                        OrderItem::getQuantity,
                        Integer::sum
                ));
    }

    private void removeFromCart(Long userId, Set<Long> productIds) {
        cartRepository.findWithItemsByUserId(userId)
                .ifPresent(cart -> cart.getItems()
                        .removeIf(item -> productIds.contains(item.getProduct().getId())));
    }
}