package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, String> {
    Optional<Inventory> findByProductProductId(String productId);

    @Query("SELECT i FROM Inventory i WHERE i.product.store.storeId = :storeId")
    Page<Inventory> findByStoreId(@Param("storeId") String storeId, Pageable pageable);

    @Query("SELECT i FROM Inventory i WHERE i.product.store.storeId = :storeId AND i.quantity <= i.lowStockThreshold")
    List<Inventory> findLowStockByStoreId(@Param("storeId") String storeId);
}
