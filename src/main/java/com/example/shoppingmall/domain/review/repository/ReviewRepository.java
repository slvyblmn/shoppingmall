package com.example.shoppingmall.domain.review.repository;

import com.example.shoppingmall.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByOrderItemId(Long orderItemId);

    @Query("select r from Review r join fetch r.user where r.id = :id")
    Optional<Review> findWithUserById(@Param("id") Long id);

    @Query(value = """
            select r from Review r
            join fetch r.user
            where r.product.id = :productId
            """,
            countQuery = """
            select count(r) from Review r
            where r.product.id = :productId
            """)
    Page<Review> findByProductIdWithUser(@Param("productId") Long productId, Pageable pageable);
}