package com.example.shoppingmall.domain.order.service;

import com.example.shoppingmall.domain.order.dto.OrderCreateRequest;
import com.example.shoppingmall.domain.order.dto.OrderItemRequest;
import com.example.shoppingmall.domain.order.dto.OrderResponse;
import com.example.shoppingmall.domain.order.dto.OrderSummaryResponse;
import com.example.shoppingmall.domain.order.entity.Order;
import com.example.shoppingmall.domain.order.entity.OrderItem;
import com.example.shoppingmall.domain.order.entity.ShippingInfo;
import com.example.shoppingmall.domain.order.repository.OrderRepository;
import com.example.shoppingmall.domain.product.entity.Product;
import com.example.shoppingmall.domain.product.entity.ProductStatus;
import com.example.shoppingmall.domain.product.service.ProductService;
import com.example.shoppingmall.domain.user.entity.User;
import com.example.shoppingmall.domain.user.service.UserService;
import com.example.shoppingmall.global.common.PageResponse;
import com.example.shoppingmall.global.exception.BusinessException;
import com.example.shoppingmall.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ProductService productService;

    @Transactional
    public OrderResponse create(Long userId, OrderCreateRequest request) {
        User user = userService.getActiveUser(userId);

        Map<Long, Integer> quantities = request.items().stream()
                .collect(Collectors.toMap(
                        OrderItemRequest::productId,
                        OrderItemRequest::quantity,
                        Integer::sum,
                        LinkedHashMap::new
                ));

        List<OrderItem> orderItems = quantities.entrySet().stream()
                .map(entry -> {
                    Product product = productService.getProduct(entry.getKey());
                    validateOrderable(product, entry.getValue());
                    return OrderItem.create(product, entry.getValue());
                })
                .toList();

        ShippingInfo shippingInfo = ShippingInfo.of(
                request.receiverName(),
                request.receiverPhone(),
                request.address()
        );

        Order order = Order.create(user, shippingInfo, orderItems);
        orderRepository.save(order);
        return OrderResponse.from(order);
    }

    public PageResponse<OrderSummaryResponse> getMyOrders(Long userId, Pageable pageable) {
        return PageResponse.from(
                orderRepository.findByUserId(userId, pageable).map(OrderSummaryResponse::from)
        );
    }

    public OrderResponse getMyOrder(Long userId, Long orderId) {
        return OrderResponse.from(getOwnedOrder(userId, orderId));
    }

    @Transactional
    public OrderResponse cancel(Long userId, Long orderId) {
        Order order = getOwnedOrder(userId, orderId);
        order.cancel();
        return OrderResponse.from(order);
    }

    public Order getOwnedOrder(Long userId, Long orderId) {
        return orderRepository.findWithItemsById(orderId)
                .filter(order -> order.isOwnedBy(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    private void validateOrderable(Product product, int quantity) {
        if (product.isDeleted() || product.getStatus() != ProductStatus.ON_SALE) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_ON_SALE);
        }
        if (quantity > product.getStock()) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }
    }
}