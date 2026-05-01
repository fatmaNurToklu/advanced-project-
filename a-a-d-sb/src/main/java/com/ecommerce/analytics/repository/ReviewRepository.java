package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, String> {
    Page<Review> findByProductProductId(String productId, Pageable pageable);
    List<Review> findByUserUserId(String userId);
    boolean existsByUserUserIdAndProductProductId(String userId, String productId);

    @Query("SELECT AVG(r.starRating) FROM Review r WHERE r.product.productId = :productId")
    Double avgRatingByProductId(@Param("productId") String productId);

    @Query("SELECT AVG(r.starRating) FROM Review r WHERE r.product.store.storeId = :storeId")
    Double avgRatingByStoreId(@Param("storeId") String storeId);

    long countByProductProductId(String productId);

    Page<Review> findByProductStoreStoreId(String storeId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.store.storeId = :storeId")
    long countByStoreId(@Param("storeId") String storeId);
}
