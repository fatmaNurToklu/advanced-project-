package com.ecommerce.analytics.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerProfile {

    @Id
    @Column(name = "profile_id")
    private String profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private Integer age;
    private String city;

    @Column(name = "membership_type")
    private String membershipType;

    @Column(name = "total_spend", precision = 10, scale = 2)
    private BigDecimal totalSpend;

    @Column(name = "satisfaction_level")
    private Integer satisfactionLevel;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @PrePersist
    protected void onCreate() {
        if (profileId == null) profileId = UUID.randomUUID().toString();
        if (totalSpend == null) totalSpend = BigDecimal.ZERO;
    }
}
