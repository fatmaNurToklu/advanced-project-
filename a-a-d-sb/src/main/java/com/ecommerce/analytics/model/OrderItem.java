package com.ecommerce.analytics.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItem {

    @Id
    @Column(name = "order_item_id")
    private String orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;

    @Column(name = "unit_price_at_sale", precision = 10, scale = 2)
    private BigDecimal unitPriceAtSale;

    @Column(name = "total_item_price", precision = 10, scale = 2)
    private BigDecimal totalItemPrice;

    @PrePersist
    protected void onCreate() {
        if (orderItemId == null) orderItemId = UUID.randomUUID().toString();
    }
}
