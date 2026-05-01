package com.ecommerce.analytics.controller;

import com.ecommerce.analytics.dto.request.*;
import com.ecommerce.analytics.dto.response.*;
import com.ecommerce.analytics.service.*;
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


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Individual User")
public class IndividualController {

    private final UserService userService;
    private final CartService cartService;
    private final OrderService orderService;
    private final IndividualService individualService;

    // ── Profile ──────────────────────────────────────────────────────────────

    @GetMapping("/users/profile")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(userService.getProfile(principal.getUsername()));
    }

    @PutMapping("/users/profile")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(principal.getUsername(), request));
    }

    // ── Addresses ────────────────────────────────────────────────────────────

    @GetMapping("/users/addresses")
    @Operation(summary = "Get all saved addresses")
    public ResponseEntity<List<AddressResponse>> getAddresses(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(userService.getAddresses(principal.getUsername()));
    }

    @PostMapping("/users/addresses")
    @Operation(summary = "Add a new address")
    public ResponseEntity<AddressResponse> addAddress(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(userService.addAddress(principal.getUsername(), request));
    }

    @DeleteMapping("/users/addresses/{addressId}")
    @Operation(summary = "Delete an address")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String addressId) {
        userService.deleteAddress(principal.getUsername(), addressId);
        return ResponseEntity.noContent().build();
    }

    // ── Cart ─────────────────────────────────────────────────────────────────

    @GetMapping("/cart")
    @Operation(summary = "Get current cart")
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(cartService.getCart(principal.getUsername()));
    }

    @PostMapping("/cart/items")
    @Operation(summary = "Add or update item in cart")
    public ResponseEntity<CartResponse> addToCart(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addOrUpdateItem(principal.getUsername(), request));
    }

    @DeleteMapping("/cart/items/{productId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<CartResponse> removeFromCart(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String productId) {
        return ResponseEntity.ok(cartService.removeItem(principal.getUsername(), productId));
    }

    @DeleteMapping("/cart")
    @Operation(summary = "Clear entire cart")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails principal) {
        cartService.clearCart(principal.getUsername());
        return ResponseEntity.noContent().build();
    }

    // ── Checkout / Orders ─────────────────────────────────────────────────────

    @PostMapping("/checkout")
    @Operation(summary = "Place an order from cart")
    public ResponseEntity<OrderResponse> checkout(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(orderService.checkout(principal.getUsername(), request));
    }

    @GetMapping("/orders")
    @Operation(summary = "Get paginated order history")
    public ResponseEntity<PagedResponse<OrderResponse>> getOrders(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        var result = orderService.getOrders(principal.getUsername(), pageable);
        return ResponseEntity.ok(PagedResponse.<OrderResponse>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build());
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> getOrder(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderById(principal.getUsername(), orderId));
    }

    @PatchMapping("/orders/{orderId}/cancel")
    @Operation(summary = "Cancel a pending order")
    public ResponseEntity<OrderResponse> cancelOrder(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(principal.getUsername(), orderId));
    }

    // ── Reviews ───────────────────────────────────────────────────────────────

    @PostMapping("/products/{productId}/reviews")
    @Operation(summary = "Submit a review for a product")
    public ResponseEntity<ReviewResponse> submitReview(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String productId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(individualService.submitReview(principal.getUsername(), productId, request));
    }

    // ── Analytics ─────────────────────────────────────────────────────────────

    @GetMapping("/users/analytics/spending")
    @Operation(summary = "Get personal spending analytics")
    public ResponseEntity<SpendingAnalyticsResponse> getSpendingAnalytics(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(individualService.getSpendingAnalytics(principal.getUsername(), from, to));
    }

    // ── Export ────────────────────────────────────────────────────────────────

    @GetMapping("/orders/export")
    @Operation(summary = "Export order history as CSV (optional date range)")
    public ResponseEntity<byte[]> exportOrders(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        String csv = orderService.exportOrdersCsv(principal.getUsername(), from, to);
        byte[] bytes = csv.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "orders.csv");
        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    // ── Coupons ───────────────────────────────────────────────────────────────

    @GetMapping("/coupons/validate")
    @Operation(summary = "Validate a coupon code")
    public ResponseEntity<Map<String, Object>> validateCoupon(@RequestParam String code) {
        return ResponseEntity.ok(individualService.validateCoupon(code));
    }
}
