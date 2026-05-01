package com.ecommerce.analytics.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShipmentRequest {
    @NotBlank
    private String orderId;
    @NotBlank
    private String addressId;
    private String carrierName;
    private String trackingNumber;
    private String warehouseBlock;
    private String modeOfShipment;
    private LocalDateTime estimatedDelivery;
}
