package com.ecommerce.analytics.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatAskRequest {
    @NotBlank
    private String sessionId;
    @NotBlank
    private String question;
}
