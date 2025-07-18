package com.company.service;

import com.company.models.dto.request.CategoryRequest;
import com.company.models.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest categoryRequest);

    List<CategoryResponse> getAllCategory();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse updateCategory(Long id,CategoryRequest categoryRequest);

    void deleteCategory(Long id);
}
