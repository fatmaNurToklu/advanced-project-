package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {
    List<Category> findByParentIsNull();
    List<Category> findByParentCategoryId(String parentId);
    Optional<Category> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
