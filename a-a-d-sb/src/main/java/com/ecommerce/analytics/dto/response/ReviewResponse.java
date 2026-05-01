package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReviewResponse {
    private String reviewId;
    private String userId;
    private String userName;
    private String productId;
    private int starRating;
    private String sentiment;
    private String comment;
    private int helpfulVotes;
    private boolean isVerifiedPurchase;
    private LocalDateTime createdAt;
    private String storeOwnerReply;
    private LocalDateTime repliedAt;
}
