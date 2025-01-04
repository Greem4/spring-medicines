package com.greem4.springmedicines.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
// fixme: Собственные комменты для валидатора - дело бессмысленное, на самом деле. Кому они мб полезны?:)
//        А вот swagger-документация была бы не лишней
        @NotBlank(message = "Старый пароль не может быть пустым")
        String oldPassword,

        @NotBlank(message = "Новый пароль не может быть пустым")
        @Size(min = 6, message = "Новый пароль должен содержать минимум 6 символов")
        String newPassword,

        // fixme: это фронтовая валидация. какой смысл на бэкенд тянуть дублирующиеся данные?
        @NotBlank(message = "Подтверждение нового пароля не может быть пустым")
        String confirmNewPassword
) {
}
