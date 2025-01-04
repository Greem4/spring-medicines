package com.greem4.springmedicines.dto;

import com.greem4.springmedicines.database.entity.Role;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank String password
) {
    // fixme: не лучшая идея. Модель редко когда должна уметь в конвертацию в другие модели. Чаще ты таким
    //  решением будешь сужать себе пространство для маневра и увеличивать связнгость кода
    public UserCreatedRequest toUserCreatedRequest() {
        return new UserCreatedRequest(username, password, Role.USER,true, "local", null);
    }
}
