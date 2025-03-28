package com.greem4.springmedicines.dto;

import com.greem4.springmedicines.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreatedRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotNull Role role,
        boolean enabled,
        String provider,
        String providerId
        ) {
}
