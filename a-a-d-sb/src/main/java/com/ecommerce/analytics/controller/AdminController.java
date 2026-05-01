package com.ecommerce.analytics.controller;

import com.ecommerce.analytics.dto.request.CategoryRequest;
import com.ecommerce.analytics.dto.request.SystemConfigRequest;
import com.ecommerce.analytics.dto.response.*;
import com.ecommerce.analytics.model.enums.StoreStatus;
import com.ecommerce.analytics.service.AdminService;
import com.ecommerce.analytics.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Platform Management")
public class AdminController {

    private final AdminService adminService;
    private final CategoryService categoryService;

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    @Operation(summary = "Platform-wide KPI dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    @GetMapping("/users")
    @Operation(summary = "List all non-admin users (paginated)")
    public ResponseEntity<PagedResponse<UserProfileResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var result = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(PagedResponse.<UserProfileResponse>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build());
    }

    @PatchMapping("/users/{userId}/status")
    @Operation(summary = "Enable or disable a user account")
    public ResponseEntity<UserProfileResponse> updateUserStatus(
            @PathVariable String userId,
            @RequestParam boolean status,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(adminService.updateUserStatus(userId, status, principal.getUsername()));
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Delete a user account")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String userId,
            @AuthenticationPrincipal UserDetails principal) {
        adminService.deleteUser(userId, principal.getUsername());
        return ResponseEntity.noContent().build();
    }

    // ── Stores ────────────────────────────────────────────────────────────────

    @GetMapping("/stores")
    @Operation(summary = "List all stores (paginated)")
    public ResponseEntity<PagedResponse<StoreResponse>> getAllStores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var result = adminService.getAllStores(pageable);
        return ResponseEntity.ok(PagedResponse.<StoreResponse>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build());
    }

    @PatchMapping("/stores/{storeId}/status")
    @Operation(summary = "Open or close a store")
    public ResponseEntity<StoreResponse> updateStoreStatus(
            @PathVariable String storeId,
            @RequestParam StoreStatus status,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(adminService.updateStoreStatus(storeId, status, principal.getUsername()));
    }

    // ── Reviews ───────────────────────────────────────────────────────────────

    @GetMapping("/reviews")
    @Operation(summary = "List all reviews (paginated)")
    public ResponseEntity<PagedResponse<ReviewResponse>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var result = adminService.getAllReviews(pageable);
        return ResponseEntity.ok(PagedResponse.<ReviewResponse>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build());
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "Delete a review")
    public ResponseEntity<Void> deleteReview(
            @PathVariable String reviewId,
            @AuthenticationPrincipal UserDetails principal) {
        adminService.deleteReview(reviewId, principal.getUsername());
        return ResponseEntity.noContent().build();
    }

    // ── Categories ────────────────────────────────────────────────────────────

    @PostMapping("/categories")
    @Operation(summary = "Create a new category")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "Update a category")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Delete a category")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ── Cross-Store Comparison ────────────────────────────────────────────────

    @GetMapping("/analytics/stores/comparison")
    @Operation(summary = "Cross-store comparison report (revenue, orders, rating, stock)")
    public ResponseEntity<List<StoreComparisonResponse>> getCrossStoreComparison() {
        return ResponseEntity.ok(adminService.getCrossStoreComparison());
    }

    // ── System Configuration ──────────────────────────────────────────────────

    @GetMapping("/config")
    @Operation(summary = "Get all system configuration entries")
    public ResponseEntity<List<SystemConfigResponse>> getAllConfigs() {
        return ResponseEntity.ok(adminService.getAllConfigs());
    }

    @PutMapping("/config")
    @Operation(summary = "Create or update a system configuration entry")
    public ResponseEntity<SystemConfigResponse> upsertConfig(
            @Valid @RequestBody SystemConfigRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(adminService.upsertConfig(request, principal.getUsername()));
    }

    @DeleteMapping("/config/{configKey}")
    @Operation(summary = "Delete a system configuration entry")
    public ResponseEntity<Void> deleteConfig(
            @PathVariable String configKey,
            @AuthenticationPrincipal UserDetails principal) {
        adminService.deleteConfig(configKey, principal.getUsername());
        return ResponseEntity.noContent().build();
    }

    // ── Audit Logs ────────────────────────────────────────────────────────────

    @GetMapping("/audit-logs")
    @Operation(summary = "Get audit logs (filter by entityType optionally)")
    public ResponseEntity<PagedResponse<AuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        var result = adminService.getAuditLogs(entityType, pageable);
        return ResponseEntity.ok(PagedResponse.<AuditLogResponse>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build());
    }
}
