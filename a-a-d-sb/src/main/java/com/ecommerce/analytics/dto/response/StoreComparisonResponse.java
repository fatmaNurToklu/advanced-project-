package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StoreComparisonResponse {
    private String storeId;
    private String storeName;
    private String status;
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long pendingOrders;
    private long totalProducts;
    private long lowStockCount;
    private Double avgRating;
    private long totalReviews;
}
