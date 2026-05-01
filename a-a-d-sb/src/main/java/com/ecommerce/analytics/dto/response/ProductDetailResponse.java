package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductDetailResponse {
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
    private List<String> imageUrls;
    private String primaryImageUrl;
    private Double avgRating;
    private long reviewCount;
    private String inventoryStatus;
    private int quantity;
    private List<ReviewResponse> reviews;
}
