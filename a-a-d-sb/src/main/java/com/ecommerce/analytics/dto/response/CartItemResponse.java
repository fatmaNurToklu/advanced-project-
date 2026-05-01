package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CartItemResponse {
    private String cartItemId;
    private String productId;
    private String productName;
    private String primaryImageUrl;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal subtotal;
}
