package com.example.shoppingmall.domain.delivery.repository;

import com.example.shoppingmall.domain.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    boolean existsByOrderId(Long orderId);

    @Query("select d from Delivery d join fetch d.order where d.id = :id")
    Optional<Delivery> findWithOrderById(@Param("id") Long id);

    @Query("select d from Delivery d join fetch d.order where d.order.id = :orderId")
    Optional<Delivery> findWithOrderByOrderId(@Param("orderId") Long orderId);
}