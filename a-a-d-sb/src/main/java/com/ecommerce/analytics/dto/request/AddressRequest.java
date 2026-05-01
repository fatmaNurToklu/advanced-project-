package com.ecommerce.analytics.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {
    @NotBlank
    private String addressTitle;
    @NotBlank
    private String addressLine;
    @NotBlank
    private String city;
    private String state;
    @NotBlank
    private String country;
    private String postalCode;
    private boolean isDefault;
}
