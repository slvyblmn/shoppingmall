package com.example.shoppingmall.domain.product.repository;

import com.example.shoppingmall.domain.product.entity.Product;
import com.example.shoppingmall.domain.product.entity.ProductStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = "category")
    Optional<Product> findByIdAndDeletedFalse(Long id);

    boolean existsByCategoryId(Long categoryId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id in :ids order by p.id")
    List<Product> findAllByIdInForUpdate(@Param("ids") Collection<Long> ids);

    @Query(value = """
            select p from Product p
            join fetch p.category c
            where p.deleted = false
              and p.status = :status
              and (:categoryId is null or c.id = :categoryId)
              and (:keyword is null or p.name like concat('%', :keyword, '%'))
            """,
            countQuery = """
            select count(p) from Product p
            where p.deleted = false
              and p.status = :status
              and (:categoryId is null or p.category.id = :categoryId)
              and (:keyword is null or p.name like concat('%', :keyword, '%'))
            """)
    Page<Product> search(@Param("categoryId") Long categoryId,
                         @Param("keyword") String keyword,
                         @Param("status") ProductStatus status,
                         Pageable pageable);
}