package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.Order;
import com.ecommerce.analytics.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findByUserUserIdOrderByOrderDateDesc(String userId, Pageable pageable);
    Page<Order> findByStoreStoreIdOrderByOrderDateDesc(String storeId, Pageable pageable);
    Optional<Order> findByOrderIdAndUserUserId(String orderId, String userId);
    Optional<Order> findByOrderIdAndStoreStoreId(String orderId, String storeId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.store.storeId = :storeId AND o.status = 'Completed'")
    BigDecimal totalRevenueByStoreId(@Param("storeId") String storeId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.store.storeId = :storeId AND o.status = 'Completed' AND o.orderDate BETWEEN :from AND :to")
    BigDecimal revenueByStoreIdAndDateRange(@Param("storeId") String storeId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    long countByStoreStoreId(String storeId);
    long countByStoreStoreIdAndStatus(String storeId, OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'Completed'")
    BigDecimal totalPlatformRevenue();

    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.user.userId = :userId AND o.status = 'Completed'")
    BigDecimal totalSpendByUserId(@Param("userId") String userId);

    List<Order> findByUserUserIdAndStatus(String userId, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.user.userId = :userId AND (:from IS NULL OR o.orderDate >= :from) AND (:to IS NULL OR o.orderDate <= :to) ORDER BY o.orderDate DESC")
    List<Order> findByUserForExport(@Param("userId") String userId,
                                    @Param("from") LocalDateTime from,
                                    @Param("to") LocalDateTime to);
}
