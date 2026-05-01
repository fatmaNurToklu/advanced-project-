package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SpendingAnalyticsResponse {
    private BigDecimal totalSpend;
    private long totalOrders;
    private long completedOrders;
    private long cancelledOrders;
    private BigDecimal avgOrderValue;
    private Map<String, BigDecimal> spendByCategory;
    private List<OrderResponse> recentOrders;
}
