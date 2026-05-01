package com.ecommerce.analytics.controller;

import com.ecommerce.analytics.dto.request.ProductRequest;
import com.ecommerce.analytics.dto.request.ReviewReplyRequest;
import com.ecommerce.analytics.dto.request.ShipmentRequest;
import com.ecommerce.analytics.dto.response.*;
import com.ecommerce.analytics.service.CorporateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/corporate")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Corporate / Store Management")
public class CorporateController {

    private final CorporateService corporateService;

    // ── Store ────────────────────────────────────────────────────────────────

    @GetMapping("/store")
    @Operation(summary = "Get own store info")
    public ResponseEntity<StoreResponse> getStore(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(corporateService.getStore(principal.getUsername()));
    }

    @PutMapping("/store")
    @Operation(summary = "Update store name / description")
    public ResponseEntity<StoreResponse> updateStore(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(required = false) String storeName,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(corporateService.updateStore(principal.getUsername(), storeName, description));
    }

    // ── Products ─────────────────────────────────────────────────────────────

    @PostMapping("/products")
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductResponse> createProduct(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(corporateService.createProduct(principal.getUsername(), request));
    }

    @PutMapping("/products/{productId}")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<ProductResponse> updateProduct(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String productId,
            @RequestBody ProductRequest request) {
        return ResponseEntity.ok(corporateService.updateProduct(principal.getUsername(), productId, request));
    }

    @DeleteMapping("/products/{productId}")
    @Operation(summary = "Deactivate (soft-delete) a product")
    public ResponseEntity<Void> deleteProduct(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String productId) {
        corporateService.deleteProduct(principal.getUsername(), productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/products/{productId}/images")
    @Operation(summary = "Add image to a product")
    public ResponseEntity<Void> addProductImage(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String productId,
            @RequestParam String imageUrl,
            @RequestParam(defaultValue = "false") boolean isPrimary) {
        corporateService.addProductImage(principal.getUsername(), productId, imageUrl, isPrimary);
        return ResponseEntity.noContent().build();
    }

    // ── Inventory ─────────────────────────────────────────────────────────────

    @GetMapping("/inventory")
    @Operation(summary = "Get store inventory (with low-stock flags)")
    public ResponseEntity<PagedResponse<InventoryResponse>> getInventory(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("lastStockUpdate").descending());
        var result = corporateService.getInventory(principal.getUsername(), pageable);
        return ResponseEntity.ok(PagedResponse.<InventoryResponse>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build());
    }

    // ── Orders ────────────────────────────────────────────────────────────────

    @GetMapping("/orders")
    @Operation(summary = "Get store orders")
    public ResponseEntity<PagedResponse<OrderResponse>> getOrders(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        var result = corporateService.getOrders(principal.getUsername(), pageable);
        return ResponseEntity.ok(PagedResponse.<OrderResponse>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build());
    }

    // ── Shipments ─────────────────────────────────────────────────────────────

    @PostMapping("/shipments")
    @Operation(summary = "Create a shipment for an order")
    public ResponseEntity<ShipmentResponse> createShipment(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody ShipmentRequest request) {
        return ResponseEntity.ok(corporateService.createShipment(principal.getUsername(), request));
    }

    // ── Dashboard & Analytics ─────────────────────────────────────────────────

    @GetMapping("/dashboard")
    @Operation(summary = "Store performance dashboard")
    public ResponseEntity<StoreDashboardResponse> getDashboard(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(corporateService.getDashboard(principal.getUsername()));
    }

    @GetMapping("/analytics/revenue")
    @Operation(summary = "Revenue analytics for a date range")
    public ResponseEntity<RevenueAnalyticsResponse> getRevenueAnalytics(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(corporateService.getRevenueAnalytics(principal.getUsername(), from, to));
    }

    // ── Customer Segmentation ─────────────────────────────────────────────────

    @GetMapping("/analytics/customers/segments")
    @Operation(summary = "Customer segmentation by city, membership, or age_group")
    public ResponseEntity<List<CustomerSegmentResponse>> getCustomerSegmentation(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "city") String segmentBy) {
        return ResponseEntity.ok(corporateService.getCustomerSegmentation(principal.getUsername(), segmentBy));
    }

    // ── Reviews ───────────────────────────────────────────────────────────────

    @GetMapping("/reviews")
    @Operation(summary = "Get all reviews for store products")
    public ResponseEntity<PagedResponse<ReviewResponse>> getStoreReviews(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var result = corporateService.getStoreReviews(principal.getUsername(), pageable);
        return ResponseEntity.ok(PagedResponse.<ReviewResponse>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build());
    }

    @PostMapping("/reviews/{reviewId}/reply")
    @Operation(summary = "Reply to a customer review")
    public ResponseEntity<ReviewResponse> replyToReview(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String reviewId,
            @Valid @RequestBody ReviewReplyRequest request) {
        return ResponseEntity.ok(corporateService.replyToReview(principal.getUsername(), reviewId, request));
    }
}
