package com.company.models.mapper;

import com.company.models.dto.request.CategoryRequest;
import com.company.models.dto.response.CategoryResponse;
import com.company.models.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CategoryRequest categoryRequest);

    CategoryResponse toCategoryResponse(Category category);

}
