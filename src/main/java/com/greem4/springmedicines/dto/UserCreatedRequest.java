package com.greem4.springmedicines.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreatedRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotNull String role
) {

}
