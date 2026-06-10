package com.savia.auth_service.dto;

import com.savia.auth_service.entity.Role;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String firstname,
        String lastname,
        String email,
        Role role
) {
}