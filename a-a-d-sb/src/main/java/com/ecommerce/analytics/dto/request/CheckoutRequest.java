package com.ecommerce.analytics.dto.request;

import com.ecommerce.analytics.model.enums.PaymentMethodType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotBlank
    private String addressId;
    @NotNull
    private PaymentMethodType paymentMethod;
    private String couponCode;
}
