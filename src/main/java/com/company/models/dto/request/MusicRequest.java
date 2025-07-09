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
    @NotBlank
    private String title;

    @NotEmpty
    private String description;

    @NotNull
    private Set<Long> allCategoryId;
}
