package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {
    Page<Product> findByIsActiveTrue(Pageable pageable);
    Page<Product> findByStoreStoreIdAndIsActiveTrue(String storeId, Pageable pageable);
    Page<Product> findByCategoryCategoryIdAndIsActiveTrue(String categoryId, Pageable pageable);
    List<Product> findByStoreStoreId(String storeId);
    Optional<Product> findByProductIdAndIsActiveTrue(String productId);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:categoryId IS NULL OR p.category.categoryId = :categoryId OR p.category.parent.categoryId = :categoryId) AND " +
           "(:minPrice IS NULL OR p.basePrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.basePrice <= :maxPrice)")
    Page<Product> searchByKeyword(@Param("keyword") String keyword,
                                   @Param("categoryId") String categoryId,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(:categoryId IS NULL OR p.category.categoryId = :categoryId OR p.category.parent.categoryId = :categoryId) AND " +
           "(:minPrice IS NULL OR p.basePrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.basePrice <= :maxPrice)")
    Page<Product> filterProducts(@Param("categoryId") String categoryId,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice,
                                  Pageable pageable);

    long countByStoreStoreId(String storeId);
}
