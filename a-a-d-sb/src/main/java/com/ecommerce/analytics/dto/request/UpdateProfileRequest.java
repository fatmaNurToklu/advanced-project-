package com.ecommerce.analytics.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String gender;
    private Integer age;
    private String city;
    private String membershipType;
}
