package com.ecommerce.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CategoryResponse {
    private String categoryId;
    private String name;
    private String slug;
    private String iconUrl;
    private String parentId;
    private List<CategoryResponse> children;
}
