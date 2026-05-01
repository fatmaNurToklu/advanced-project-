package com.ecommerce.analytics.service;

import com.ecommerce.analytics.dto.request.SystemConfigRequest;
import com.ecommerce.analytics.dto.response.*;
import com.ecommerce.analytics.exception.ResourceNotFoundException;
import com.ecommerce.analytics.model.*;
import com.ecommerce.analytics.model.enums.OrderStatus;
import com.ecommerce.analytics.model.enums.RoleType;
import com.ecommerce.analytics.model.enums.StoreStatus;
import com.ecommerce.analytics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final ReviewRepository reviewRepository;
    private final AuditLogRepository auditLogRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final InventoryRepository inventoryRepository;

    // ── Users ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<UserProfileResponse> getAllUsers(Pageable pageable) {
        return userRepository.findByRoleNot(RoleType.ADMIN, pageable).map(this::toUserProfile);
    }

    @Transactional
    public UserProfileResponse updateUserStatus(String userId, boolean status, String adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setStatus(status);
        saveAuditLog(status ? "USER_ENABLED" : "USER_SUSPENDED", "User", userId,
                adminId, "User status changed to: " + status);
        return toUserProfile(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(String userId, String adminId) {
        if (!userRepository.existsById(userId))
            throw new ResourceNotFoundException("User not found: " + userId);
        userRepository.deleteById(userId);
        saveAuditLog("USER_DELETED", "User", userId, adminId, "User account deleted");
    }

    // ── Stores ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<StoreResponse> getAllStores(Pageable pageable) {
        return storeRepository.findAll(pageable).map(this::toStoreResponse);
    }

    @Transactional
    public StoreResponse updateStoreStatus(String storeId, StoreStatus status, String adminId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found: " + storeId));
        store.setStatus(status);
        saveAuditLog("STORE_STATUS_CHANGED", "Store", storeId,
                adminId, "Store status changed to: " + status);
        return toStoreResponse(storeRepository.save(store));
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {
        long totalUsers = userRepository.count();
        long totalCustomers = userRepository.findByRole(RoleType.CUSTOMER, Pageable.unpaged()).getTotalElements();
        long totalCorporate = userRepository.findByRole(RoleType.CORPORATE, Pageable.unpaged()).getTotalElements();
        long totalStores = storeRepository.count();
        long openStores = storeRepository.countByStatus(StoreStatus.Open);
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.Pending);
        long totalProducts = productRepository.count();

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalCustomers(totalCustomers)
                .totalCorporateUsers(totalCorporate)
                .totalStores(totalStores)
                .openStores(openStores)
                .closedStores(totalStores - openStores)
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .platformRevenue(orderRepository.totalPlatformRevenue())
                .totalProducts(totalProducts)
                .build();
    }

    // ── Reviews ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable).map(r -> {
            try { return toReviewResponse(r); }
            catch (Exception e) { return toReviewResponseSafe(r); }
        });
    }

    @Transactional
    public void deleteReview(String reviewId, String adminId) {
        if (!reviewRepository.existsById(reviewId))
            throw new ResourceNotFoundException("Review not found: " + reviewId);
        reviewRepository.deleteById(reviewId);
        saveAuditLog("REVIEW_DELETED", "Review", reviewId, adminId, "Review deleted by admin");
    }

    // ── Cross-Store Comparison ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<StoreComparisonResponse> getCrossStoreComparison() {
        return storeRepository.findAll().stream()
                .map(store -> {
                    String storeId = store.getStoreId();
                    return StoreComparisonResponse.builder()
                            .storeId(storeId)
                            .storeName(store.getStoreName())
                            .status(store.getStatus().name())
                            .totalRevenue(orderRepository.totalRevenueByStoreId(storeId))
                            .totalOrders(orderRepository.countByStoreStoreId(storeId))
                            .pendingOrders(orderRepository.countByStoreStoreIdAndStatus(storeId, OrderStatus.Pending))
                            .totalProducts(productRepository.countByStoreStoreId(storeId))
                            .lowStockCount(inventoryRepository.findLowStockByStoreId(storeId).size())
                            .avgRating(reviewRepository.avgRatingByStoreId(storeId))
                            .totalReviews(reviewRepository.countByStoreId(storeId))
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ── System Configuration ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<SystemConfigResponse> getAllConfigs() {
        return systemConfigRepository.findAll().stream()
                .map(this::toConfigResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SystemConfigResponse upsertConfig(SystemConfigRequest request, String adminId) {
        SystemConfig config = systemConfigRepository.findById(request.getConfigKey())
                .orElse(SystemConfig.builder().configKey(request.getConfigKey()).build());
        config.setConfigValue(request.getConfigValue());
        config.setDescription(request.getDescription());
        config.setUpdatedBy(adminId);
        config = systemConfigRepository.save(config);
        saveAuditLog("CONFIG_UPDATED", "SystemConfig", request.getConfigKey(),
                adminId, "Config value set to: " + request.getConfigValue());
        return toConfigResponse(config);
    }

    @Transactional
    public void deleteConfig(String configKey, String adminId) {
        if (!systemConfigRepository.existsById(configKey))
            throw new ResourceNotFoundException("Config not found: " + configKey);
        systemConfigRepository.deleteById(configKey);
        saveAuditLog("CONFIG_DELETED", "SystemConfig", configKey, adminId, "Config deleted");
    }

    // ── Audit Logs ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogs(String entityType, Pageable pageable) {
        Page<AuditLog> logs = entityType != null
                ? auditLogRepository.findByEntityTypeOrderByCreatedAtDesc(entityType, pageable)
                : auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        return logs.map(this::toAuditLogResponse);
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    public void saveAuditLog(String action, String entityType, String entityId,
                              String adminId, String details) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .performedByUserId(adminId)
                .details(details)
                .build();
        auditLogRepository.save(log);
    }

    private ReviewResponse toReviewResponse(Review r) {
        String userId = r.getUser() != null ? r.getUser().getUserId() : "unknown";
        String userName = r.getUser() != null
                ? r.getUser().getFirstName() + " " + r.getUser().getLastName()
                : "Unknown User";
        String productId = r.getProduct() != null ? r.getProduct().getProductId() : "unknown";
        return ReviewResponse.builder()
                .reviewId(r.getReviewId())
                .userId(userId)
                .userName(userName)
                .productId(productId)
                .starRating(r.getStarRating())
                .sentiment(r.getSentiment())
                .comment(r.getComment())
                .helpfulVotes(r.getHelpfulVotes())
                .isVerifiedPurchase(r.isVerifiedPurchase())
                .createdAt(r.getCreatedAt())
                .storeOwnerReply(r.getStoreOwnerReply())
                .repliedAt(r.getRepliedAt())
                .build();
    }

    private ReviewResponse toReviewResponseSafe(Review r) {
        return ReviewResponse.builder()
                .reviewId(r.getReviewId())
                .userId("unknown")
                .userName("Unknown User")
                .productId("unknown")
                .starRating(r.getStarRating())
                .sentiment(r.getSentiment())
                .comment(r.getComment())
                .helpfulVotes(r.getHelpfulVotes())
                .isVerifiedPurchase(r.isVerifiedPurchase())
                .createdAt(r.getCreatedAt())
                .storeOwnerReply(r.getStoreOwnerReply())
                .repliedAt(r.getRepliedAt())
                .build();
    }

    private UserProfileResponse toUserProfile(User user) {
        var profile = customerProfileRepository.findByUserUserId(user.getUserId()).orElse(null);
        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender())
                .role(user.getRole().name())
                .status(user.isStatus())
                .age(profile != null ? profile.getAge() : null)
                .city(profile != null ? profile.getCity() : null)
                .membershipType(profile != null ? profile.getMembershipType() : null)
                .totalSpend(profile != null ? profile.getTotalSpend() : null)
                .createdAt(user.getCreatedAt())
                .build();
    }

    private StoreResponse toStoreResponse(Store s) {
        return StoreResponse.builder()
                .storeId(s.getStoreId())
                .storeName(s.getStoreName())
                .description(s.getDescription())
                .status(s.getStatus().name())
                .storeRating(s.getStoreRating())
                .ownerName(s.getOwner().getFirstName() + " " + s.getOwner().getLastName())
                .createdAt(s.getCreatedAt())
                .build();
    }

    private SystemConfigResponse toConfigResponse(SystemConfig c) {
        return SystemConfigResponse.builder()
                .configKey(c.getConfigKey())
                .configValue(c.getConfigValue())
                .description(c.getDescription())
                .updatedAt(c.getUpdatedAt())
                .updatedBy(c.getUpdatedBy())
                .build();
    }

    private AuditLogResponse toAuditLogResponse(AuditLog l) {
        return AuditLogResponse.builder()
                .logId(l.getLogId())
                .action(l.getAction())
                .entityType(l.getEntityType())
                .entityId(l.getEntityId())
                .performedByUserId(l.getPerformedByUserId())
                .performedByEmail(l.getPerformedByEmail())
                .details(l.getDetails())
                .createdAt(l.getCreatedAt())
                .build();
    }
}
