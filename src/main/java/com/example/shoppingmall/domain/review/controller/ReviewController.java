package com.example.shoppingmall.domain.review.controller;

import com.example.shoppingmall.domain.review.dto.ReviewCreateRequest;
import com.example.shoppingmall.domain.review.dto.ReviewResponse;
import com.example.shoppingmall.domain.review.dto.ReviewUpdateRequest;
import com.example.shoppingmall.domain.review.service.ReviewService;
import com.example.shoppingmall.global.common.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/products/{productId}/reviews")
    public ResponseEntity<ReviewResponse> create(@AuthenticationPrincipal Long userId,
                                                 @PathVariable Long productId,
                                                 @Valid @RequestBody ReviewCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.create(userId, productId, request));
    }

    @GetMapping("/api/products/{productId}/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId, pageable));
    }

    @PatchMapping("/api/reviews/{id}")
    public ResponseEntity<ReviewResponse> update(@AuthenticationPrincipal Long userId,
                                                 @PathVariable Long id,
                                                 @Valid @RequestBody ReviewUpdateRequest request) {
        return ResponseEntity.ok(reviewService.update(userId, id, request));
    }

    @DeleteMapping("/api/reviews/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Long userId,
                                       @PathVariable Long id) {
        reviewService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}