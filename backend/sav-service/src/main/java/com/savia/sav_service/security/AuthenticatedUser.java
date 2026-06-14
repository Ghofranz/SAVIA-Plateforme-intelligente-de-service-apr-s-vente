package com.savia.sav_service.security;

public record AuthenticatedUser(
        Long userId,
        String email,
        String role
) {
}