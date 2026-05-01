package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StoreResponse {
    private String storeId;
    private String storeName;
    private String description;
    private String status;
    private BigDecimal storeRating;
    private String ownerName;
    private LocalDateTime createdAt;
}
