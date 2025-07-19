package com.company.controller;

import com.company.models.dto.ApiResponseDto;
import com.company.models.dto.request.AuthenticationRequest;
import com.company.models.dto.request.RefreshTokenRequest;
import com.company.models.dto.request.RegistrationRequest;
import com.company.models.dto.response.AuthenticationResponse;
import com.company.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/auth")
@Validated
@Tag(name = "Authentication")
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<Void>> register(
            @Valid @RequestBody RegistrationRequest request) {

        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponseDto<>("User created successfully", null)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<AuthenticationResponse>> login(
            @Valid @RequestBody AuthenticationRequest request) {

        return ResponseEntity.ok(
                new ApiResponseDto<>("User logged in successfully",
                        authService.login(request))
        );
    }

    @GetMapping("/activate-account")
    public void confirm(@NotBlank(message = "Token must not be blank")
                        @RequestParam String token) {

        authService.activateAccount(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(
          @Valid @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

        return null;
    }

}
