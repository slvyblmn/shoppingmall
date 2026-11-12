package com.example.shoppingmall.domain.review.service;

import com.example.shoppingmall.domain.order.entity.OrderItem;
import com.example.shoppingmall.domain.order.entity.OrderStatus;
import com.example.shoppingmall.domain.order.repository.OrderItemRepository;
import com.example.shoppingmall.domain.product.entity.Product;
import com.example.shoppingmall.domain.product.service.ProductService;
import com.example.shoppingmall.domain.review.dto.ReviewCreateRequest;
import com.example.shoppingmall.domain.review.dto.ReviewResponse;
import com.example.shoppingmall.domain.review.dto.ReviewUpdateRequest;
import com.example.shoppingmall.domain.review.entity.Review;
import com.example.shoppingmall.domain.review.repository.ReviewRepository;
import com.example.shoppingmall.domain.user.entity.User;
import com.example.shoppingmall.domain.user.service.UserService;
import com.example.shoppingmall.global.common.PageResponse;
import com.example.shoppingmall.global.exception.BusinessException;
import com.example.shoppingmall.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;
    private final ProductService productService;

    @Transactional
    public ReviewResponse create(Long userId, Long productId, ReviewCreateRequest request) {
        User user = userService.getActiveUser(userId);
        Product product = productService.getProduct(productId);

        List<OrderItem> deliveredItems = orderItemRepository
                .findByUserIdAndProductIdAndOrderStatus(userId, productId, OrderStatus.DELIVERED);
        if (deliveredItems.isEmpty()) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_ALLOWED);
        }

        OrderItem target = deliveredItems.stream()
                .filter(item -> !reviewRepository.existsByOrderItemId(item.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ALREADY_REVIEWED));

        Review review = Review.create(user, product, target, request.rating(), request.content());
        try {
            reviewRepository.saveAndFlush(review);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.ALREADY_REVIEWED);
        }

        return ReviewResponse.from(review);
    }

    public PageResponse<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        productService.getProduct(productId);
        return PageResponse.from(
                reviewRepository.findByProductIdWithUser(productId, pageable).map(ReviewResponse::from)
        );
    }

    @Transactional
    public ReviewResponse update(Long userId, Long reviewId, ReviewUpdateRequest request) {
        Review review = getOwnReview(userId, reviewId);
        review.update(request.rating(), request.content());
        return ReviewResponse.from(review);
    }

    @Transactional
    public void delete(Long userId, Long reviewId) {
        reviewRepository.delete(getOwnReview(userId, reviewId));
    }

    private Review getOwnReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findWithUserById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        if (!review.isWrittenBy(userId)) {
            throw new BusinessException(ErrorCode.REVIEW_FORBIDDEN);
        }
        return review;
    }
}