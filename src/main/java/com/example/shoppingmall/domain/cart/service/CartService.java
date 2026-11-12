package com.example.shoppingmall.domain.cart.service;

import com.example.shoppingmall.domain.cart.dto.CartItemAddRequest;
import com.example.shoppingmall.domain.cart.dto.CartItemUpdateRequest;
import com.example.shoppingmall.domain.cart.dto.CartResponse;
import com.example.shoppingmall.domain.cart.entity.Cart;
import com.example.shoppingmall.domain.cart.entity.CartItem;
import com.example.shoppingmall.domain.cart.repository.CartRepository;
import com.example.shoppingmall.domain.product.entity.Product;
import com.example.shoppingmall.domain.product.entity.ProductStatus;
import com.example.shoppingmall.domain.product.service.ProductService;
import com.example.shoppingmall.domain.user.service.UserService;
import com.example.shoppingmall.global.exception.BusinessException;
import com.example.shoppingmall.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final ProductService productService;

    public CartResponse getCart(Long userId) {
        return cartRepository.findWithItemsByUserId(userId)
                .map(CartResponse::from)
                .orElseGet(CartResponse::empty);
    }

    @Transactional
    public CartResponse addItem(Long userId, CartItemAddRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = productService.getProduct(request.productId());
        validateOnSale(product);

        Optional<CartItem> existing = cart.findItemByProductId(product.getId());
        int newQuantity = existing.map(CartItem::getQuantity).orElse(0) + request.quantity();
        validateStock(product, newQuantity);

        existing.ifPresentOrElse(
                item -> item.changeQuantity(newQuantity),
                () -> cart.addItem(product, newQuantity)
        );

        cartRepository.flush();
        return CartResponse.from(cart);
    }

    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long cartItemId, CartItemUpdateRequest request) {
        Cart cart = getCartEntity(userId);
        CartItem item = getCartItem(cart, cartItemId);
        Product product = item.getProduct();

        validateOnSale(product);
        validateStock(product, request.quantity());

        item.changeQuantity(request.quantity());
        return CartResponse.from(cart);
    }

    @Transactional
    public void removeItem(Long userId, Long cartItemId) {
        Cart cart = getCartEntity(userId);
        cart.removeItem(getCartItem(cart, cartItemId));
    }

    @Transactional
    public void clear(Long userId) {
        cartRepository.findWithItemsByUserId(userId).ifPresent(Cart::clear);
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findWithItemsByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.create(userService.getActiveUser(userId))));
    }

    private Cart getCartEntity(Long userId) {
        return cartRepository.findWithItemsByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));
    }

    private CartItem getCartItem(Cart cart, Long cartItemId) {
        return cart.findItem(cartItemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));
    }

    private void validateOnSale(Product product) {
        if (product.isDeleted() || product.getStatus() != ProductStatus.ON_SALE) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_ON_SALE);
        }
    }

    private void validateStock(Product product, int quantity) {
        if (quantity > product.getStock()) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }
    }
}