package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserProfileResponse {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;
    private String role;
    private boolean status;
    private Integer age;
    private String city;
    private String membershipType;
    private BigDecimal totalSpend;
    private Integer satisfactionLevel;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}
