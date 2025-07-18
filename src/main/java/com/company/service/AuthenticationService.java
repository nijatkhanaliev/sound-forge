package com.company.service;

import com.company.models.dto.request.AuthenticationRequest;
import com.company.models.dto.request.RefreshTokenRequest;
import com.company.models.dto.request.RegistrationRequest;
import com.company.models.dto.response.AuthenticationResponse;
import jakarta.mail.MessagingException;

public interface AuthenticationService {
    void register(RegistrationRequest request);

    AuthenticationResponse login(AuthenticationRequest request);

    void activateAccount(String token);

    AuthenticationResponse refreshToken(RefreshTokenRequest request);

    void logout();
}
