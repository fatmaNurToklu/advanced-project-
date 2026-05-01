package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.CustomerProfile;
import com.ecommerce.analytics.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, String> {
    Optional<CustomerProfile> findByUser(User user);
    Optional<CustomerProfile> findByUserUserId(String userId);
}
