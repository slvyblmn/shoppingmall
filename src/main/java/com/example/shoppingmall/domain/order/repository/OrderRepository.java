package com.example.shoppingmall.domain.order.repository;

import com.example.shoppingmall.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
            select distinct o from Order o
            join fetch o.orderItems
            where o.id = :orderId
            """)
    Optional<Order> findWithItemsById(@Param("orderId") Long orderId);

    Page<Order> findByUserId(Long userId, Pageable pageable);
}