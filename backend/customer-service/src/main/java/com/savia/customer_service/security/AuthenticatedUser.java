package com.savia.customer_service.security;

public record AuthenticatedUser(
        Long userId,
        String email,
        String role
) {
}