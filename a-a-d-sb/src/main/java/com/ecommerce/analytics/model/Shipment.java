package com.ecommerce.analytics.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Shipment {

    @Id
    @Column(name = "shipment_id")
    private String shipmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private UserAddress address;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "carrier_name")
    private String carrierName;

    @Column(name = "warehouse_block")
    private String warehouseBlock;

    @Column(name = "mode_of_shipment")
    private String modeOfShipment;

    @Column(name = "shipping_status")
    @Builder.Default
    private String shippingStatus = "Processing";

    @Column(name = "estimated_delivery")
    private LocalDateTime estimatedDelivery;

    @PrePersist
    protected void onCreate() {
        if (shipmentId == null) shipmentId = UUID.randomUUID().toString();
    }
}
