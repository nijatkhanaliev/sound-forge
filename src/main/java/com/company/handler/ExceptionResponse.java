package com.company.handler;

import lombok.*;

import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {
    String errorMessage;
    Set<String> validationErrors;
}
