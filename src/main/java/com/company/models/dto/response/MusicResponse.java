package com.company.models.dto.response;

import com.company.models.entity.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class MusicResponse {
    private long id;
    private String title;
    private String description;
    private String url;
    private Set<Category> categories;
    private Long userId;
    private String userFullName;
}
