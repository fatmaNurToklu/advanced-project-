package com.ecommerce.analytics.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review {

    @Id
    @Column(name = "review_id")
    private String reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "star_rating")
    private int starRating;

    private String sentiment;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "helpful_votes")
    @Builder.Default
    private int helpfulVotes = 0;

    @Column(name = "is_verified_purchase")
    @Builder.Default
    private boolean isVerifiedPurchase = false;

    @Column(name = "store_owner_reply", columnDefinition = "TEXT")
    private String storeOwnerReply;

    @Column(name = "replied_at")
    private LocalDateTime repliedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (reviewId == null) reviewId = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
    }
}
