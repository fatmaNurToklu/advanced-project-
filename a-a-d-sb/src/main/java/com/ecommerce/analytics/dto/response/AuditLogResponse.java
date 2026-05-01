package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuditLogResponse {
    private String logId;
    private String action;
    private String entityType;
    private String entityId;
    private String performedByUserId;
    private String performedByEmail;
    private String details;
    private LocalDateTime createdAt;
}
