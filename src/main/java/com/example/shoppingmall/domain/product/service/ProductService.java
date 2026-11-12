package com.example.shoppingmall.domain.product.service;

import com.example.shoppingmall.domain.category.entity.Category;
import com.example.shoppingmall.domain.category.service.CategoryService;
import com.example.shoppingmall.domain.product.dto.ProductCreateRequest;
import com.example.shoppingmall.domain.product.dto.ProductResponse;
import com.example.shoppingmall.domain.product.dto.ProductUpdateRequest;
import com.example.shoppingmall.domain.product.entity.Product;
import com.example.shoppingmall.domain.product.entity.ProductStatus;
import com.example.shoppingmall.domain.product.repository.ProductRepository;
import com.example.shoppingmall.global.common.PageResponse;
import com.example.shoppingmall.global.exception.BusinessException;
import com.example.shoppingmall.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Transactional
    public ProductResponse create(ProductCreateRequest request) {
        Category category = categoryService.getCategory(request.categoryId());
        Product product = Product.create(
                category,
                request.name(),
                request.description(),
                request.price(),
                request.stock()
        );
        productRepository.save(product);
        return ProductResponse.from(product);
    }

    public PageResponse<ProductResponse> search(Long categoryId, String keyword, Pageable pageable) {
        String trimmedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        Page<ProductResponse> page = productRepository
                .search(categoryId, trimmedKeyword, ProductStatus.ON_SALE, pageable)
                .map(ProductResponse::from);
        return PageResponse.from(page);
    }

    public ProductResponse get(Long id) {
        return ProductResponse.from(getProduct(id));
    }

    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = getProduct(id);
        Category category = request.categoryId() != null
                ? categoryService.getCategory(request.categoryId())
                : null;

        product.update(
                category,
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.status()
        );
        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long id) {
        getProduct(id).delete();
    }

    public Product getProduct(Long id) {
        return productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}