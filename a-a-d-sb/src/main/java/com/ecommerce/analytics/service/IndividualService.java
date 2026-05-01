package com.ecommerce.analytics.service;

import com.ecommerce.analytics.dto.request.ReviewRequest;
import com.ecommerce.analytics.dto.response.ReviewResponse;
import com.ecommerce.analytics.dto.response.SpendingAnalyticsResponse;
import com.ecommerce.analytics.exception.ResourceNotFoundException;
import com.ecommerce.analytics.model.Coupon;
import com.ecommerce.analytics.model.Product;
import com.ecommerce.analytics.model.Review;
import com.ecommerce.analytics.model.User;
import com.ecommerce.analytics.model.enums.OrderStatus;
import com.ecommerce.analytics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IndividualService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CouponRepository couponRepository;

    @Transactional
    public ReviewResponse submitReview(String userId, String productId, ReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        boolean verified = orderItemRepository.existsByOrderUserUserIdAndProductProductId(userId, productId);
        String sentiment = request.getStarRating() >= 4 ? "positive" : request.getStarRating() == 3 ? "neutral" : "negative";

        Review review = Review.builder()
                .user(user)
                .product(product)
                .starRating(request.getStarRating())
                .comment(request.getComment())
                .sentiment(sentiment)
                .isVerifiedPurchase(verified)
                .build();
        review = reviewRepository.save(review);

        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .userId(userId)
                .userName(user.getFirstName() + " " + user.getLastName())
                .productId(productId)
                .starRating(review.getStarRating())
                .sentiment(review.getSentiment())
                .comment(review.getComment())
                .isVerifiedPurchase(review.isVerifiedPurchase())
                .createdAt(review.getCreatedAt())
                .storeOwnerReply(review.getStoreOwnerReply())
                .repliedAt(review.getRepliedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public SpendingAnalyticsResponse getSpendingAnalytics(String userId, LocalDateTime from, LocalDateTime to) {
        BigDecimal totalSpend = orderRepository.totalSpendByUserId(userId);
        long totalOrders = orderRepository.findByUserUserIdOrderByOrderDateDesc(userId, org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
        long completedOrders = orderRepository.findByUserUserIdAndStatus(userId, OrderStatus.Completed).size();
        long cancelledOrders = orderRepository.findByUserUserIdAndStatus(userId, OrderStatus.Cancelled).size();
        BigDecimal avgOrderValue = totalOrders > 0
                ? totalSpend.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return SpendingAnalyticsResponse.builder()
                .totalSpend(totalSpend)
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .avgOrderValue(avgOrderValue)
                .build();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> validateCoupon(String code) {
        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found or inactive: " + code));
        return Map.of(
                "code", coupon.getCode(),
                "discountPercentage", coupon.getDiscountPercentage(),
                "isActive", coupon.isActive()
        );
    }
}
