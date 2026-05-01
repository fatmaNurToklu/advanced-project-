package com.ecommerce.analytics.service;

import com.ecommerce.analytics.dto.request.CategoryRequest;
import com.ecommerce.analytics.dto.response.CategoryResponse;
import com.ecommerce.analytics.exception.ResourceNotFoundException;
import com.ecommerce.analytics.model.Category;
import com.ecommerce.analytics.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        List<Category> roots = categoryRepository.findByParentIsNull();
        return roots.stream().map(this::toResponseWithChildren).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
        return toResponseWithChildren(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        return createCategory(request.getName(), request.getSlug(), request.getIconUrl(), request.getParentId());
    }

    @Transactional
    public CategoryResponse updateCategory(String categoryId, CategoryRequest request) {
        return updateCategory(categoryId, request.getName(), request.getSlug(), request.getIconUrl());
    }

    @Transactional
    public CategoryResponse createCategory(String name, String slug, String iconUrl, String parentId) {
        Category parent = parentId != null ? categoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent category not found")) : null;
        Category category = Category.builder()
                .name(name).slug(slug).iconUrl(iconUrl).parent(parent).build();
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(String categoryId, String name, String slug, String iconUrl) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
        if (name != null) category.setName(name);
        if (slug != null) category.setSlug(slug);
        if (iconUrl != null) category.setIconUrl(iconUrl);
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(String categoryId) {
        if (!categoryRepository.existsById(categoryId))
            throw new ResourceNotFoundException("Category not found: " + categoryId);
        categoryRepository.deleteById(categoryId);
    }

    private CategoryResponse toResponseWithChildren(Category c) {
        List<Category> children = categoryRepository.findByParentCategoryId(c.getCategoryId());
        return CategoryResponse.builder()
                .categoryId(c.getCategoryId())
                .name(c.getName())
                .slug(c.getSlug())
                .iconUrl(c.getIconUrl())
                .parentId(c.getParent() != null ? c.getParent().getCategoryId() : null)
                .children(children.stream().map(this::toResponseWithChildren).collect(Collectors.toList()))
                .build();
    }

    private CategoryResponse toResponse(Category c) {
        return CategoryResponse.builder()
                .categoryId(c.getCategoryId())
                .name(c.getName())
                .slug(c.getSlug())
                .iconUrl(c.getIconUrl())
                .parentId(c.getParent() != null ? c.getParent().getCategoryId() : null)
                .build();
    }
}
