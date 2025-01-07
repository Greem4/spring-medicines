package com.greem4.springmedicines.dto;

import com.greem4.springmedicines.domain.Role;

public record UserResponse(
        Long id,
        String username,
        Role role,
        boolean enabled
) {
}
