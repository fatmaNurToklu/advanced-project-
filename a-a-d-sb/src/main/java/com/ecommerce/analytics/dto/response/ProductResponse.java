package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductResponse {
    private String productId;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private String categoryId;
    private String categoryName;
    private String storeId;
    private String storeName;
    private String sku;
    private boolean isActive;
    private String primaryImageUrl;
    private Double avgRating;
    private long reviewCount;
    private String inventoryStatus;
    private int quantity;
}
