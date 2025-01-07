package com.greem4.springmedicines.mapper;

import com.greem4.springmedicines.domain.User;
import com.greem4.springmedicines.dto.UserResponse;

public class UserResponseMap {

    public static UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.isEnabled()
        );
    }
}
