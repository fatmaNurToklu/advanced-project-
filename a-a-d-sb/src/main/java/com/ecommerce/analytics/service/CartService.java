package com.ecommerce.analytics.service;

import com.ecommerce.analytics.dto.request.CartItemRequest;
import com.ecommerce.analytics.dto.response.CartItemResponse;
import com.ecommerce.analytics.dto.response.CartResponse;
import com.ecommerce.analytics.exception.ResourceNotFoundException;
import com.ecommerce.analytics.model.Cart;
import com.ecommerce.analytics.model.CartItem;
import com.ecommerce.analytics.model.Product;
import com.ecommerce.analytics.model.User;
import com.ecommerce.analytics.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductImageRepository productImageRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public CartResponse getCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        return toCartResponse(cart);
    }

    @Transactional
    public CartResponse addOrUpdateItem(String userId, CartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findByProductIdAndIsActiveTrue(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getProductId()));

        Optional<CartItem> existing = cartItemRepository.findByCartCartIdAndProductProductId(cart.getCartId(), request.getProductId());
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(request.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
        }
        entityManager.flush();
        entityManager.refresh(cart);
        return toCartResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(String userId, String productId) {
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cartItemRepository.findByCartCartIdAndProductProductId(cart.getCartId(), productId)
                .ifPresent(cartItemRepository::delete);
        entityManager.flush();
        entityManager.refresh(cart);
        return toCartResponse(cart);
    }

    @Transactional
    public void clearCart(String userId) {
        cartRepository.findByUserUserId(userId).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
            return cartRepository.save(Cart.builder().user(user).build());
        });
    }

    private CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream().map(item -> {
            String imageUrl = productImageRepository
                    .findByProductProductIdAndIsPrimaryTrue(item.getProduct().getProductId())
                    .map(img -> img.getImageUrl()).orElse(null);
            BigDecimal subtotal = item.getProduct().getBasePrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            return CartItemResponse.builder()
                    .cartItemId(item.getCartItemId())
                    .productId(item.getProduct().getProductId())
                    .productName(item.getProduct().getName())
                    .primaryImageUrl(imageUrl)
                    .unitPrice(item.getProduct().getBasePrice())
                    .quantity(item.getQuantity())
                    .subtotal(subtotal)
                    .build();
        }).collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getCartId())
                .items(items)
                .totalAmount(total)
                .totalItems(items.stream().mapToInt(CartItemResponse::getQuantity).sum())
                .build();
    }
}
