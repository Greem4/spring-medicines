package com.greem4.springmedicines.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Старый пароль не может быть пустым")
        String oldPassword,

        @NotBlank(message = "Новый пароль не может быть пустым")
        @Size(min = 6, message = "Новый пароль должен содержать минимум 6 символов")
        String newPassword,

        @NotBlank(message = "Подтверждение нового пароля не может быть пустым")
        String confirmNewPassword
) {
}
