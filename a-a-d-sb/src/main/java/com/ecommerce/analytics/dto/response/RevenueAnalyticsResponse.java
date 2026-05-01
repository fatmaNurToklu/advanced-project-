package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RevenueAnalyticsResponse {
    private BigDecimal totalRevenue;
    private BigDecimal periodRevenue;
    private Map<String, BigDecimal> revenueByMonth;
    private Map<String, BigDecimal> revenueByCategory;
    private BigDecimal avgOrderValue;
    private long totalOrders;
}
