package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
    Optional<CartItem> findByCartCartIdAndProductProductId(String cartId, String productId);
    void deleteByCartCartIdAndCartItemId(String cartId, String cartItemId);
}
