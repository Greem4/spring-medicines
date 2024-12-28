package com.greem4.springmedicines.dto;

import com.greem4.springmedicines.database.entity.Role;

public record UserResponse(
        Long id,
        String username,
        Role role,
        boolean enable
) {
}
