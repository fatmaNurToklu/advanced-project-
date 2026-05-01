package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigRepository extends JpaRepository<SystemConfig, String> {
}
