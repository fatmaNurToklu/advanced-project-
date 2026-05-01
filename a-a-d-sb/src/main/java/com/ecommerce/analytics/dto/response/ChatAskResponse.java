package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChatAskResponse {
    private String sessionId;
    private String question;
    private String answer;
    private String sqlQuery;
    private String visualizationCode;
    private boolean hasVisualization;
}
