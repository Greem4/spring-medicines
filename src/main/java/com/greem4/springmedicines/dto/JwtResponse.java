package com.greem4.springmedicines.dto;

import com.greem4.springmedicines.database.entity.Role;

public record JwtResponse(
        String token,
        String type,
        Long id,
        String username,
        Role role
) {
}
