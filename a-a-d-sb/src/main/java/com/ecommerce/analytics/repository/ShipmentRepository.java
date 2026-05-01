package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, String> {
    Optional<Shipment> findByOrderOrderId(String orderId);
    List<Shipment> findByOrderStoreStoreId(String storeId);
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
}
