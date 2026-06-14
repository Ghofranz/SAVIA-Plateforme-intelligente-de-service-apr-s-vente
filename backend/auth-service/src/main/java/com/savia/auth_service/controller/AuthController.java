package com.savia.auth_service.controller;

import com.savia.auth_service.dto.AuthResponse;
import com.savia.auth_service.dto.LoginRequest;
import com.savia.auth_service.dto.RegisterRequest;
import com.savia.auth_service.dto.UserProfileResponse;
import com.savia.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserProfileResponse getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return authService.getCurrentUser(email);
    }
}