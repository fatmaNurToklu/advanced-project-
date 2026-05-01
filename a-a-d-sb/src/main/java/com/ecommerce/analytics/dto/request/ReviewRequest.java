package com.ecommerce.analytics.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewRequest {
    @Min(1) @Max(5)
    private int starRating;
    @NotBlank
    private String comment;
}
