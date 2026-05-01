package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, String> {
    List<ProductImage> findByProductProductIdOrderByDisplayOrderAsc(String productId);
    Optional<ProductImage> findByProductProductIdAndIsPrimaryTrue(String productId);
}
