package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AddressResponse {
    private String addressId;
    private String addressTitle;
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private boolean isDefault;
    private boolean isActive;
}
