package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderResponse {
    private String orderId;
    private String orderNumber;
    private String storeId;
    private String storeName;
    private BigDecimal totalAmount;
    private String status;
    private String fulfilment;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;
    private ShipmentResponse shipment;
    private PaymentResponse payment;
}
