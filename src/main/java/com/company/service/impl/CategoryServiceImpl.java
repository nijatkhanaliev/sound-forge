package com.company.service.impl;

import com.company.exception.AlreadyExistsException;
import com.company.exception.NotFoundException;
import com.company.models.dto.request.CategoryRequest;
import com.company.models.dto.response.CategoryResponse;
import com.company.models.entity.Category;
import com.company.models.mapper.CategoryMapper;
import com.company.repository.CategoryRepository;
import com.company.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Optional<Category> category = categoryRepository.findByName(categoryRequest.getName());

        if (category.isPresent()) {
            throw new AlreadyExistsException("Category already exists");
        }
        log.info("Creating new category");
        Category newCategory = categoryMapper.toCategory(categoryRequest);
        categoryRepository.save(newCategory);

        return categoryMapper.toCategoryResponse(newCategory);
    }

    @Override
    public List<CategoryResponse> getAllCategory() {
        log.info("Getting all category");
        List<Category> allCategory = categoryRepository.findAll();

        return allCategory.stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));

        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        log.info("Updating category, categoryId: {}", id);
        category.setName(categoryRequest.getName());
        categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        log.info("Deleting category, categoryId: {}", id);
        categoryRepository.delete(category);
    }
}
