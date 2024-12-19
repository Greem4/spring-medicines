package com.greem4.springmedicines.http.controller;

import com.greem4.springmedicines.database.entity.User;
import com.greem4.springmedicines.dto.UserCreatedRequest;
import com.greem4.springmedicines.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    public final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody UserCreatedRequest request) {
        if (userService.existsByUsername(request.username())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        return userService.createUser(request);
    }
}
