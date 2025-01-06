package com.greem4.springmedicines.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank String username,
        @NotBlank String oldPassword,
        @NotBlank String newPassword,
        @NotBlank String confirmNewPassword
) {
}
