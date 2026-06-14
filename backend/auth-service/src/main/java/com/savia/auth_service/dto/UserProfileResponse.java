package com.savia.auth_service.dto;

import com.savia.auth_service.entity.Role;

public record UserProfileResponse(
        Long id,
        String firstname,
        String lastname,
        String email,
        Role role,
        boolean enabled
) {
}