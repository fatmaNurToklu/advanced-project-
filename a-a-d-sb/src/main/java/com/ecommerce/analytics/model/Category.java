package com.ecommerce.analytics.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Category {

    @Id
    @Column(name = "category_id")
    private String categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Column(nullable = false)
    private String name;

    private String slug;

    @Column(name = "icon_url")
    private String iconUrl;

    @PrePersist
    protected void onCreate() {
        if (categoryId == null) categoryId = UUID.randomUUID().toString();
    }
}
