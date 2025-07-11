package com.company.models.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class MusicRequest {
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotEmpty(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "categoryId cannot be null")
    private Set<Long> allCategoryId;
}
