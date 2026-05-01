package com.ecommerce.analytics.service;

import com.ecommerce.analytics.dto.request.ProductRequest;
import com.ecommerce.analytics.dto.request.ReviewReplyRequest;
import com.ecommerce.analytics.dto.request.ShipmentRequest;
import com.ecommerce.analytics.dto.response.*;
import com.ecommerce.analytics.exception.ResourceNotFoundException;
import com.ecommerce.analytics.model.*;
import com.ecommerce.analytics.model.enums.InventoryStatus;
import com.ecommerce.analytics.model.enums.OrderStatus;
import com.ecommerce.analytics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CorporateService {

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;
    private final UserAddressRepository userAddressRepository;
    private final ReviewRepository reviewRepository;
    private final ProductImageRepository productImageRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final ProductService productService;
    private final OrderService orderService;

    // ── Store ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public StoreResponse getStore(String userId) {
        return toStoreResponse(getStoreByOwner(userId));
    }

    @Transactional
    public StoreResponse updateStore(String userId, String storeName, String description) {
        Store store = getStoreByOwner(userId);
        if (storeName != null) store.setStoreName(storeName);
        if (description != null) store.setDescription(description);
        return toStoreResponse(storeRepository.save(store));
    }

    // ── Products ─────────────────────────────────────────────────────────────

    @Transactional
    public ProductResponse createProduct(String userId, ProductRequest request) {
        Store store = getStoreByOwner(userId);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = Product.builder()
                .store(store)
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .sku(request.getSku())
                .basePrice(request.getBasePrice())
                .costOfProduct(request.getCostOfProduct())
                .isActive(true)
                .build();
        productRepository.save(product);

        Inventory inventory = Inventory.builder()
                .product(product)
                .quantity(request.getInitialStock())
                .lowStockThreshold(request.getLowStockThreshold() > 0 ? request.getLowStockThreshold() : 10)
                .status(request.getInitialStock() > 0 ? InventoryStatus.IN_STOCK : InventoryStatus.OUT_OF_STOCK)
                .build();
        inventoryRepository.save(inventory);

        return productService.toProductResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(String userId, String productId, ProductRequest request) {
        Product product = getOwnedProduct(userId, productId);

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getBasePrice() != null) product.setBasePrice(request.getBasePrice());
        if (request.getCostOfProduct() != null) product.setCostOfProduct(request.getCostOfProduct());
        if (request.getSku() != null) product.setSku(request.getSku());
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }
        productRepository.save(product);

        if (request.getInitialStock() > 0) {
            inventoryRepository.findByProductProductId(productId).ifPresent(inv -> {
                inv.setQuantity(request.getInitialStock());
                inv.setStatus(request.getInitialStock() > 0 ? InventoryStatus.IN_STOCK : InventoryStatus.OUT_OF_STOCK);
                if (request.getLowStockThreshold() > 0) inv.setLowStockThreshold(request.getLowStockThreshold());
                inventoryRepository.save(inv);
            });
        }

        return productService.toProductResponse(product);
    }

    @Transactional
    public void deleteProduct(String userId, String productId) {
        Product product = getOwnedProduct(userId, productId);
        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional
    public void addProductImage(String userId, String productId, String imageUrl, boolean isPrimary) {
        Product product = getOwnedProduct(userId, productId);

        if (isPrimary) {
            productImageRepository.findByProductProductIdAndIsPrimaryTrue(productId)
                    .ifPresent(img -> { img.setPrimary(false); productImageRepository.save(img); });
        }

        List<ProductImage> existing = productImageRepository.findByProductProductIdOrderByDisplayOrderAsc(productId);
        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .isPrimary(isPrimary)
                .displayOrder(existing.size())
                .build();
        productImageRepository.save(image);
    }

    // ── Inventory ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<InventoryResponse> getInventory(String userId, Pageable pageable) {
        Store store = getStoreByOwner(userId);
        List<Inventory> lowStock = inventoryRepository.findLowStockByStoreId(store.getStoreId());
        return inventoryRepository.findByStoreId(store.getStoreId(), pageable)
                .map(inv -> InventoryResponse.builder()
                        .inventoryId(inv.getInventoryId())
                        .productId(inv.getProduct().getProductId())
                        .productName(inv.getProduct().getName())
                        .quantity(inv.getQuantity())
                        .lowStockThreshold(inv.getLowStockThreshold())
                        .binLocation(inv.getBinLocation())
                        .status(inv.getStatus().name())
                        .isLowStock(lowStock.stream().anyMatch(l -> l.getInventoryId().equals(inv.getInventoryId())))
                        .lastStockUpdate(inv.getLastStockUpdate())
                        .build());
    }

    // ── Orders ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrders(String userId, Pageable pageable) {
        Store store = getStoreByOwner(userId);
        return orderRepository.findByStoreStoreIdOrderByOrderDateDesc(store.getStoreId(), pageable)
                .map(orderService::buildOrderResponse);
    }

    // ── Shipments ─────────────────────────────────────────────────────────────

    @Transactional
    public ShipmentResponse createShipment(String userId, ShipmentRequest request) {
        Store store = getStoreByOwner(userId);
        Order order = orderRepository.findByOrderIdAndStoreStoreId(request.getOrderId(), store.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        UserAddress address = userAddressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Shipment shipment = Shipment.builder()
                .order(order)
                .address(address)
                .carrierName(request.getCarrierName())
                .trackingNumber(request.getTrackingNumber())
                .warehouseBlock(request.getWarehouseBlock())
                .modeOfShipment(request.getModeOfShipment())
                .estimatedDelivery(request.getEstimatedDelivery())
                .shippingStatus("Processing")
                .build();
        shipment = shipmentRepository.save(shipment);
        order.setStatus(OrderStatus.Shipped);
        orderRepository.save(order);

        return ShipmentResponse.builder()
                .shipmentId(shipment.getShipmentId())
                .orderId(order.getOrderId())
                .trackingNumber(shipment.getTrackingNumber())
                .carrierName(shipment.getCarrierName())
                .modeOfShipment(shipment.getModeOfShipment())
                .shippingStatus(shipment.getShippingStatus())
                .estimatedDelivery(shipment.getEstimatedDelivery())
                .build();
    }

    // ── Dashboard & Analytics ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public StoreDashboardResponse getDashboard(String userId) {
        Store store = getStoreByOwner(userId);
        String storeId = store.getStoreId();
        return StoreDashboardResponse.builder()
                .totalRevenue(orderRepository.totalRevenueByStoreId(storeId))
                .totalOrders(orderRepository.countByStoreStoreId(storeId))
                .pendingOrders(orderRepository.countByStoreStoreIdAndStatus(storeId, OrderStatus.Pending))
                .completedOrders(orderRepository.countByStoreStoreIdAndStatus(storeId, OrderStatus.Completed))
                .cancelledOrders(orderRepository.countByStoreStoreIdAndStatus(storeId, OrderStatus.Cancelled))
                .totalProducts(productRepository.countByStoreStoreId(storeId))
                .lowStockCount(inventoryRepository.findLowStockByStoreId(storeId).size())
                .avgRating(reviewRepository.avgRatingByStoreId(storeId))
                .totalReviews(reviewRepository.countByStoreId(storeId))
                .build();
    }

    @Transactional(readOnly = true)
    public RevenueAnalyticsResponse getRevenueAnalytics(String userId, LocalDateTime from, LocalDateTime to) {
        Store store = getStoreByOwner(userId);
        String storeId = store.getStoreId();
        BigDecimal totalRevenue = orderRepository.totalRevenueByStoreId(storeId);
        BigDecimal periodRevenue = orderRepository.revenueByStoreIdAndDateRange(storeId, from, to);
        long totalOrders = orderRepository.countByStoreStoreId(storeId);
        BigDecimal avg = totalOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return RevenueAnalyticsResponse.builder()
                .totalRevenue(totalRevenue)
                .periodRevenue(periodRevenue)
                .avgOrderValue(avg)
                .totalOrders(totalOrders)
                .build();
    }

    // ── Customer Segmentation ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CustomerSegmentResponse> getCustomerSegmentation(String userId, String segmentBy) {
        getStoreByOwner(userId);
        List<CustomerProfile> profiles = customerProfileRepository.findAll();

        if ("city".equalsIgnoreCase(segmentBy)) {
            return aggregateSegments(profiles, "city",
                    p -> p.getCity() != null ? p.getCity() : "Unknown");
        } else if ("membership".equalsIgnoreCase(segmentBy)) {
            return aggregateSegments(profiles, "membership",
                    p -> p.getMembershipType() != null ? p.getMembershipType() : "Unknown");
        } else {
            return aggregateSegments(profiles, "age_group", p -> {
                if (p.getAge() == null) return "Unknown";
                if (p.getAge() < 25) return "18-24";
                if (p.getAge() < 35) return "25-34";
                if (p.getAge() < 45) return "35-44";
                if (p.getAge() < 55) return "45-54";
                return "55+";
            });
        }
    }

    // ── Reviews Management ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getStoreReviews(String userId, Pageable pageable) {
        Store store = getStoreByOwner(userId);
        return reviewRepository.findByProductStoreStoreId(store.getStoreId(), pageable)
                .map(this::toReviewResponse);
    }

    @Transactional
    public ReviewResponse replyToReview(String userId, String reviewId, ReviewReplyRequest request) {
        Store store = getStoreByOwner(userId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));

        if (!review.getProduct().getStore().getStoreId().equals(store.getStoreId()))
            throw new IllegalArgumentException("This review does not belong to your store");

        review.setStoreOwnerReply(request.getReply());
        review.setRepliedAt(LocalDateTime.now());
        review = reviewRepository.save(review);
        return toReviewResponse(review);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Store getStoreByOwner(String userId) {
        return storeRepository.findByOwnerUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found for user: " + userId));
    }

    private Product getOwnedProduct(String userId, String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        Store store = getStoreByOwner(userId);
        if (!product.getStore().getStoreId().equals(store.getStoreId()))
            throw new IllegalArgumentException("Product does not belong to your store");
        return product;
    }

    private List<CustomerSegmentResponse> aggregateSegments(
            List<CustomerProfile> profiles,
            String segmentType,
            java.util.function.Function<CustomerProfile, String> keyFn) {

        Map<String, List<CustomerProfile>> grouped = new LinkedHashMap<>();
        for (CustomerProfile p : profiles) {
            grouped.computeIfAbsent(keyFn.apply(p), k -> new ArrayList<>()).add(p);
        }

        return grouped.entrySet().stream().map(e -> {
            List<CustomerProfile> group = e.getValue();
            BigDecimal totalSpend = group.stream()
                    .map(p -> p.getTotalSpend() != null ? p.getTotalSpend() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avgSpend = group.isEmpty() ? BigDecimal.ZERO
                    : totalSpend.divide(BigDecimal.valueOf(group.size()), 2, RoundingMode.HALF_UP);
            return CustomerSegmentResponse.builder()
                    .segmentType(segmentType)
                    .segmentValue(e.getKey())
                    .customerCount(group.size())
                    .totalSpend(totalSpend)
                    .avgSpend(avgSpend)
                    .build();
        }).collect(Collectors.toList());
    }

    private ReviewResponse toReviewResponse(Review r) {
        return ReviewResponse.builder()
                .reviewId(r.getReviewId())
                .userId(r.getUser().getUserId())
                .userName(r.getUser().getFirstName() + " " + r.getUser().getLastName())
                .productId(r.getProduct().getProductId())
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
}
