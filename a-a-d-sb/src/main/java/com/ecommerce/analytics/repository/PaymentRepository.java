package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByOrderOrderId(String orderId);
}
