package com.ecommerce.analytics.service;

import com.ecommerce.analytics.dto.response.ProductDetailResponse;
import com.ecommerce.analytics.dto.response.ProductResponse;
import com.ecommerce.analytics.dto.response.ReviewResponse;
import com.ecommerce.analytics.exception.ResourceNotFoundException;
import com.ecommerce.analytics.model.Inventory;
import com.ecommerce.analytics.model.Product;
import com.ecommerce.analytics.model.ProductImage;
import com.ecommerce.analytics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ReviewRepository reviewRepository;
    private final ProductImageRepository productImageRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String keyword, String categoryId,
                                                BigDecimal minPrice, BigDecimal maxPrice,
                                                Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return productRepository.searchByKeyword(keyword.trim(), categoryId, minPrice, maxPrice, pageable)
                    .map(this::toProductResponse);
        }
        return productRepository.filterProducts(categoryId, minPrice, maxPrice, pageable)
                .map(this::toProductResponse);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProductById(String productId) {
        Product product = productRepository.findByProductIdAndIsActiveTrue(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        List<ProductImage> images = productImageRepository.findByProductProductIdOrderByDisplayOrderAsc(productId);
        Optional<Inventory> inventory = inventoryRepository.findByProductProductId(productId);
        Double avgRating = reviewRepository.avgRatingByProductId(productId);
        long reviewCount = reviewRepository.countByProductProductId(productId);

        List<ReviewResponse> reviews = reviewRepository.findByProductProductId(productId, Pageable.ofSize(10))
                .getContent().stream().map(r -> ReviewResponse.builder()
                        .reviewId(r.getReviewId())
                        .userId(r.getUser().getUserId())
                        .userName(r.getUser().getFirstName() + " " + r.getUser().getLastName())
                        .starRating(r.getStarRating())
                        .sentiment(r.getSentiment())
                        .comment(r.getComment())
                        .helpfulVotes(r.getHelpfulVotes())
                        .isVerifiedPurchase(r.isVerifiedPurchase())
                        .createdAt(r.getCreatedAt())
                        .build()).collect(Collectors.toList());

        return ProductDetailResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .basePrice(product.getBasePrice())
                .categoryId(product.getCategory().getCategoryId())
                .categoryName(product.getCategory().getName())
                .storeId(product.getStore().getStoreId())
                .storeName(product.getStore().getStoreName())
                .sku(product.getSku())
                .isActive(product.isActive())
                .imageUrls(images.stream().map(ProductImage::getImageUrl).collect(Collectors.toList()))
                .primaryImageUrl(images.stream().filter(ProductImage::isPrimary).map(ProductImage::getImageUrl).findFirst().orElse(null))
                .avgRating(avgRating)
                .reviewCount(reviewCount)
                .inventoryStatus(inventory.map(i -> i.getStatus().name()).orElse("IN_STOCK"))
                .quantity(inventory.map(i -> i.getQuantity() > 0 ? i.getQuantity() : 100).orElse(100))
                .reviews(reviews)
                .build();
    }

    public ProductResponse toProductResponse(Product product) {
        Optional<Inventory> inventory = inventoryRepository.findByProductProductId(product.getProductId());
        Optional<ProductImage> primaryImage = productImageRepository.findByProductProductIdAndIsPrimaryTrue(product.getProductId());
        Double avgRating = reviewRepository.avgRatingByProductId(product.getProductId());
        long reviewCount = reviewRepository.countByProductProductId(product.getProductId());

        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .basePrice(product.getBasePrice())
                .categoryId(product.getCategory().getCategoryId())
                .categoryName(product.getCategory().getName())
                .storeId(product.getStore().getStoreId())
                .storeName(product.getStore().getStoreName())
                .sku(product.getSku())
                .isActive(product.isActive())
                .primaryImageUrl(primaryImage.map(ProductImage::getImageUrl).orElse(null))
                .avgRating(avgRating)
                .reviewCount(reviewCount)
                .inventoryStatus(inventory.map(i -> i.getStatus().name()).orElse("IN_STOCK"))
                .quantity(inventory.map(i -> i.getQuantity() > 0 ? i.getQuantity() : 100).orElse(100))
                .build();
    }
}
