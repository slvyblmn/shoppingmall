package com.example.shoppingmall.domain.order.repository;

import com.example.shoppingmall.domain.order.entity.OrderItem;
import com.example.shoppingmall.domain.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
            select oi from OrderItem oi
            join oi.order o
            where o.user.id = :userId
              and oi.product.id = :productId
              and o.status = :status
            order by oi.id
            """)
    List<OrderItem> findByUserIdAndProductIdAndOrderStatus(@Param("userId") Long userId,
                                                           @Param("productId") Long productId,
                                                           @Param("status") OrderStatus status);
}