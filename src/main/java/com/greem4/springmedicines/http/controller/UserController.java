package com.greem4.springmedicines.http.controller;

import com.greem4.springmedicines.dto.ChangePasswordRequest;
import com.greem4.springmedicines.dto.UserResponse;
import com.greem4.springmedicines.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getProfile(Principal principal) {
        var username = principal.getName();
        return userService.getUserByUsername(username);
    }

    @PutMapping("/changePassword")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            Principal principal,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        var username = principal.getName();
        userService.changePassword(username, request);
    }
}
