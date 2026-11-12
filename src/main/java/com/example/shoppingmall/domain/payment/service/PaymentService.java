package com.example.shoppingmall.domain.payment.service;

import com.example.shoppingmall.domain.order.entity.Order;
import com.example.shoppingmall.domain.order.service.OrderService;
import com.example.shoppingmall.domain.payment.dto.PaymentCancelRequest;
import com.example.shoppingmall.domain.payment.dto.PaymentConfirmRequest;
import com.example.shoppingmall.domain.payment.dto.PaymentPrepareRequest;
import com.example.shoppingmall.domain.payment.dto.PaymentPrepareResponse;
import com.example.shoppingmall.domain.payment.dto.PaymentResponse;
import com.example.shoppingmall.domain.payment.entity.Payment;
import com.example.shoppingmall.domain.payment.entity.PaymentStatus;
import com.example.shoppingmall.domain.payment.gateway.GatewayPaymentResult;
import com.example.shoppingmall.domain.payment.gateway.PaymentGateway;
import com.example.shoppingmall.domain.payment.repository.PaymentRepository;
import com.example.shoppingmall.global.exception.BusinessException;
import com.example.shoppingmall.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final String CURRENCY = "KRW";
    private static final Set<ErrorCode> AUTO_CANCEL_ERRORS = Set.of(
            ErrorCode.OUT_OF_STOCK,
            ErrorCode.PRODUCT_NOT_ON_SALE,
            ErrorCode.ORDER_NOT_PAYABLE
    );

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final PaymentProcessor paymentProcessor;
    private final PaymentGateway paymentGateway;

    @Transactional
    public PaymentPrepareResponse prepare(Long userId, PaymentPrepareRequest request) {
        Order order = orderService.getOwnedOrder(userId, request.orderId());
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);

        if (payment != null && payment.getStatus() == PaymentStatus.PAID) {
            throw new BusinessException(ErrorCode.ALREADY_PAID);
        }
        if (!order.isPending()) {
            throw new BusinessException(ErrorCode.ORDER_NOT_PAYABLE);
        }

        if (payment == null) {
            payment = paymentRepository.save(Payment.create(order));
        } else {
            payment.resetForRetry();
        }

        return new PaymentPrepareResponse(
                payment.getId(),
                payment.getPaymentId(),
                order.getOrderNumber(),
                order.summarizeItems(),
                payment.getAmount(),
                CURRENCY
        );
    }

    public PaymentResponse confirm(Long userId, PaymentConfirmRequest request) {
        Payment payment = paymentProcessor.getOwnedPayment(userId, request.paymentId());
        if (payment.getStatus() == PaymentStatus.PAID) {
            return PaymentResponse.from(payment);
        }

        GatewayPaymentResult result = paymentGateway.getPayment(payment.getPaymentId());

        return switch (result.status()) {
            case PAID -> completePaid(payment, result);
            case FAILED -> {
                paymentProcessor.markFailed(payment.getPaymentId(), result.failReason());
                throw new BusinessException(ErrorCode.PAYMENT_FAILED);
            }
            case PENDING -> throw new BusinessException(ErrorCode.PAYMENT_NOT_COMPLETED);
        };
    }

    public PaymentResponse get(Long userId, Long id) {
        return PaymentResponse.from(paymentProcessor.getOwnedPaymentById(userId, id));
    }

    public PaymentResponse cancel(Long userId, Long id, PaymentCancelRequest request) {
        Payment payment = paymentProcessor.getOwnedPaymentById(userId, id);

        if (payment.getStatus() != PaymentStatus.PAID || !payment.getOrder().isRefundable()) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_CANCELLABLE);
        }

        paymentGateway.cancelPayment(payment.getPaymentId(), request.reason());
        return paymentProcessor.completeRefund(id, request.reason());
    }

    private PaymentResponse completePaid(Payment payment, GatewayPaymentResult result) {
        String paymentId = payment.getPaymentId();

        if (!result.matches(payment.getAmount(), CURRENCY)) {
            log.warn("결제 금액 불일치: paymentId={}", paymentId);
            cancelAtGateway(paymentId, "결제 금액 불일치로 인한 자동 취소");
            paymentProcessor.markFailed(paymentId, "결제 금액 불일치");
            throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        try {
            return paymentProcessor.complete(paymentId);
        } catch (BusinessException e) {
            if (AUTO_CANCEL_ERRORS.contains(e.getErrorCode())) {
                cancelAtGateway(paymentId, "주문 처리 불가로 인한 자동 취소: " + e.getMessage());
                paymentProcessor.markFailed(paymentId, e.getMessage());
            }
            throw e;
        }
    }

    private void cancelAtGateway(String paymentId, String reason) {
        try {
            paymentGateway.cancelPayment(paymentId, reason);
        } catch (BusinessException e) {
            log.error("결제 자동 취소 실패, 수동 확인 필요: paymentId={}", paymentId);
        }
    }
}