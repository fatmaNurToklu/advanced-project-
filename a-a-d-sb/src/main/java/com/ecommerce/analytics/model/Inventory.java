package com.ecommerce.analytics.model;

import com.ecommerce.analytics.model.enums.InventoryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Inventory {

    @Id
    @Column(name = "inventory_id")
    private String inventoryId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", unique = true)
    private Product product;

    @Builder.Default
    private int quantity = 0;

    @Column(name = "low_stock_threshold")
    @Builder.Default
    private int lowStockThreshold = 10;

    @Column(name = "bin_location")
    private String binLocation;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InventoryStatus status = InventoryStatus.IN_STOCK;

    @Column(name = "last_stock_update")
    private LocalDateTime lastStockUpdate;

    @PrePersist
    protected void onCreate() {
        if (inventoryId == null) inventoryId = UUID.randomUUID().toString();
        lastStockUpdate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastStockUpdate = LocalDateTime.now();
    }
}
