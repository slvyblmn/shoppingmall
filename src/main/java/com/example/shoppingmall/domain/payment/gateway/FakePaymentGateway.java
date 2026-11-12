package com.example.shoppingmall.domain.payment.gateway;

import com.example.shoppingmall.domain.payment.entity.Payment;
import com.example.shoppingmall.domain.payment.repository.PaymentRepository;
import com.example.shoppingmall.global.exception.BusinessException;
import com.example.shoppingmall.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FakePaymentGateway implements PaymentGateway {

    private final PaymentRepository paymentRepository;

    @Override
    public GatewayPaymentResult getPayment(String paymentId) {
        Payment payment = paymentRepository.findWithOrderByPaymentId(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        log.info("[FAKE] 결제 조회: paymentId={}, amount={}", paymentId, payment.getAmount());

        return new GatewayPaymentResult(
                paymentId,
                GatewayPaymentStatus.PAID,
                payment.getAmount(),
                "KRW",
                null
        );
    }

    @Override
    public void cancelPayment(String paymentId, String reason) {
        log.info("[FAKE] 결제 취소: paymentId={}, reason={}", paymentId, reason);
    }
}