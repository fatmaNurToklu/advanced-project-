package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CustomerSegmentResponse {
    private String segmentType;
    private String segmentValue;
    private long customerCount;
    private BigDecimal totalSpend;
    private BigDecimal avgSpend;
}
