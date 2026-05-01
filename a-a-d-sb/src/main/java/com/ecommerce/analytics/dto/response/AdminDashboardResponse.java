package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminDashboardResponse {
    private long totalUsers;
    private long totalCustomers;
    private long totalCorporateUsers;
    private long totalStores;
    private long openStores;
    private long closedStores;
    private long totalOrders;
    private long pendingOrders;
    private BigDecimal platformRevenue;
    private long totalProducts;
}
