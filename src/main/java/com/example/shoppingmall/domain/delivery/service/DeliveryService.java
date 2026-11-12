package com.example.shoppingmall.domain.delivery.service;

import com.example.shoppingmall.domain.delivery.dto.DeliveryCreateRequest;
import com.example.shoppingmall.domain.delivery.dto.DeliveryResponse;
import com.example.shoppingmall.domain.delivery.dto.DeliveryUpdateRequest;
import com.example.shoppingmall.domain.delivery.entity.Delivery;
import com.example.shoppingmall.domain.delivery.repository.DeliveryRepository;
import com.example.shoppingmall.domain.order.entity.Order;
import com.example.shoppingmall.domain.order.repository.OrderRepository;
import com.example.shoppingmall.domain.order.service.OrderService;
import com.example.shoppingmall.global.exception.BusinessException;
import com.example.shoppingmall.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Transactional
    public DeliveryResponse register(DeliveryCreateRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (deliveryRepository.existsByOrderId(order.getId())) {
            throw new BusinessException(ErrorCode.DELIVERY_ALREADY_EXISTS);
        }

        Delivery delivery = Delivery.start(order, request.courier(), request.trackingNumber());
        deliveryRepository.save(delivery);
        return DeliveryResponse.from(delivery);
    }

    @Transactional
    public DeliveryResponse update(Long id, DeliveryUpdateRequest request) {
        Delivery delivery = deliveryRepository.findWithOrderById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DELIVERY_NOT_FOUND));

        delivery.updateTrackingInfo(request.courier(), request.trackingNumber());
        delivery.changeStatus(request.status());
        return DeliveryResponse.from(delivery);
    }

    public DeliveryResponse getByOrder(Long userId, Long orderId) {
        Order order = orderService.getOwnedOrder(userId, orderId);
        Delivery delivery = deliveryRepository.findWithOrderByOrderId(order.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DELIVERY_NOT_FOUND));
        return DeliveryResponse.from(delivery);
    }
}