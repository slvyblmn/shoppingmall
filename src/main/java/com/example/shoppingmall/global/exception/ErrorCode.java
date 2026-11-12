package com.example.shoppingmall.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C002", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "C003", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "C004", "만료된 토큰입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C005", "접근 권한이 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C006", "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C999", "서버 내부 오류가 발생했습니다."),

    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U001", "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U002", "회원을 찾을 수 없습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "U003", "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U004", "현재 비밀번호가 올바르지 않습니다."),
    CURRENT_PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "U005", "비밀번호를 변경하려면 현재 비밀번호가 필요합니다."),

    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CT001", "카테고리를 찾을 수 없습니다."),
    DUPLICATE_CATEGORY_NAME(HttpStatus.CONFLICT, "CT002", "이미 존재하는 카테고리 이름입니다."),
    CATEGORY_HAS_PRODUCTS(HttpStatus.CONFLICT, "CT003", "상품이 등록된 카테고리는 삭제할 수 없습니다."),

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "상품을 찾을 수 없습니다."),
    OUT_OF_STOCK(HttpStatus.CONFLICT, "P002", "재고가 부족합니다."),
    PRODUCT_NOT_ON_SALE(HttpStatus.BAD_REQUEST, "P003", "판매 중인 상품이 아닙니다."),

    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "CA001", "장바구니 상품을 찾을 수 없습니다."),

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다."),
    ORDER_CANNOT_CANCEL(HttpStatus.CONFLICT, "O002", "현재 주문 상태에서는 취소할 수 없습니다."),

    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PA001", "결제 정보를 찾을 수 없습니다."),
    ORDER_NOT_PAYABLE(HttpStatus.CONFLICT, "PA002", "결제할 수 없는 주문 상태입니다."),
    ALREADY_PAID(HttpStatus.CONFLICT, "PA003", "이미 결제가 완료된 주문입니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "PA004", "결제 금액이 주문 금액과 일치하지 않습니다."),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PA005", "결제에 실패했습니다."),
    PAYMENT_NOT_COMPLETED(HttpStatus.CONFLICT, "PA006", "결제가 아직 완료되지 않았습니다."),
    PAYMENT_NOT_CANCELLABLE(HttpStatus.CONFLICT, "PA007", "취소할 수 없는 결제입니다."),
    PAYMENT_GATEWAY_ERROR(HttpStatus.BAD_GATEWAY, "PA008", "결제 서버와 통신 중 오류가 발생했습니다."),

    DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "D001", "배송 정보를 찾을 수 없습니다."),
    DELIVERY_ALREADY_EXISTS(HttpStatus.CONFLICT, "D002", "이미 배송 정보가 등록된 주문입니다."),
    ORDER_NOT_DELIVERABLE(HttpStatus.CONFLICT, "D003", "결제 완료된 주문만 배송을 시작할 수 있습니다."),
    INVALID_DELIVERY_STATUS(HttpStatus.CONFLICT, "D004", "변경할 수 없는 배송 상태입니다."),
    TRACKING_INFO_REQUIRED(HttpStatus.BAD_REQUEST, "D005", "배송을 시작하려면 택배사와 운송장 번호가 필요합니다."),

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "리뷰를 찾을 수 없습니다."),
    REVIEW_NOT_ALLOWED(HttpStatus.FORBIDDEN, "R002", "배송 완료된 상품을 구매한 회원만 리뷰를 작성할 수 있습니다."),
    ALREADY_REVIEWED(HttpStatus.CONFLICT, "R003", "이미 리뷰를 작성한 구매입니다."),
    REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "R004", "본인이 작성한 리뷰만 수정하거나 삭제할 수 있습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}