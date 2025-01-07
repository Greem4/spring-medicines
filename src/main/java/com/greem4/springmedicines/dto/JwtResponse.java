package com.greem4.springmedicines.dto;

import com.greem4.springmedicines.domain.Role;

public record JwtResponse(
        String token,
        String type,
        int expiresIn,
        String scope,
        Long userId,
        String username,
        Role role
) {
}
