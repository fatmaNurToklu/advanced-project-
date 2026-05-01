package com.ecommerce.analytics.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemConfigRequest {
    @NotBlank
    private String configKey;
    @NotBlank
    private String configValue;
    private String description;
}
