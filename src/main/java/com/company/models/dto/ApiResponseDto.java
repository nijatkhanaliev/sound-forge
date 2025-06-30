package com.company.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApiResponseDto<T>{
    private LocalDateTime timestamp;
    private String message;
    private T object;

    public ApiResponseDto(String message, T object){
        this.message = message;
        this.object = object;
        timestamp = LocalDateTime.now();
    }
}
