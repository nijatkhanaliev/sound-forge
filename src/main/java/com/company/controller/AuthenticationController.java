package com.company.controller;

import com.company.models.dto.ApiResponseDto;
import com.company.models.dto.request.AuthenticationRequest;
import com.company.models.dto.request.RegistrationRequest;
import com.company.models.dto.response.JwtResponse;
import com.company.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/auth")
@Validated
@Tag(name = "Authentication")
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<Void>> register(@Valid @RequestBody RegistrationRequest request) throws MessagingException {
        log.info("POST /register called using request: { {} }", request);

        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponseDto<>("User created successfully", null)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<JwtResponse>> login(@Valid @RequestBody AuthenticationRequest request) {
        log.info("POST /login called using request: { {} }", request);

        return ResponseEntity.ok(
                new ApiResponseDto<>("User logged in successfully", authService.login(request))
        );
    }

    @GetMapping("/activate-account")
    public void confirm(@NotBlank(message = "Token must not be blank") @RequestParam String token){
        authService.activateAccount(token);
    }



}
