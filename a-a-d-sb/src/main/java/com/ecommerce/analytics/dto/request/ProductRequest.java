package com.ecommerce.analytics.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String categoryId;
    private String sku;
    @NotNull @DecimalMin("0.01")
    private BigDecimal basePrice;
    private BigDecimal costOfProduct;
    private int initialStock;
    private int lowStockThreshold;
}
