package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InventoryResponse {
    private String inventoryId;
    private String productId;
    private String productName;
    private int quantity;
    private int lowStockThreshold;
    private String binLocation;
    private String status;
    private boolean isLowStock;
    private LocalDateTime lastStockUpdate;
}
