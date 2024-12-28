package com.greem4.springmedicines.dto;

import com.greem4.springmedicines.database.entity.Role;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank String password
) {
    public UserCreatedRequest toUserCreatedRequest() {
        return new UserCreatedRequest(username, password, Role.USER, true);
    }
}
