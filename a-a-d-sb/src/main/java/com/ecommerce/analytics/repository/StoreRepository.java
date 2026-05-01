package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.Store;
import com.ecommerce.analytics.model.enums.StoreStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, String> {
    Optional<Store> findByOwnerUserId(String ownerId);
    Page<Store> findByStatus(StoreStatus status, Pageable pageable);
    long countByStatus(StoreStatus status);
}
