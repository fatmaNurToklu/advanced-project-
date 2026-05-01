package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, String> {
    List<UserAddress> findByUserUserIdAndIsActiveTrue(String userId);
    Optional<UserAddress> findByAddressIdAndUserUserId(String addressId, String userId);
    Optional<UserAddress> findByUserUserIdAndIsDefaultTrue(String userId);
}
