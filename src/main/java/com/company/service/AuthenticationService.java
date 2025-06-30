package com.company.service;

import com.company.models.dto.request.AuthenticationRequest;
import com.company.models.dto.request.RegistrationRequest;
import com.company.models.dto.response.JwtResponse;

public interface AuthenticationService {
    void register(RegistrationRequest request);
    JwtResponse login(AuthenticationRequest request);
}
