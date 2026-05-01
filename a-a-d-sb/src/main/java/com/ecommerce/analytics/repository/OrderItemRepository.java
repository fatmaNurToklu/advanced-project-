package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    List<OrderItem> findByOrderOrderId(String orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.store.storeId = :storeId")
    List<OrderItem> findByStoreId(@Param("storeId") String storeId);

    boolean existsByOrderUserUserIdAndProductProductId(String userId, String productId);
}
