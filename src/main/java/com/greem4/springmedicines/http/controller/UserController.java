package com.greem4.springmedicines.http.controller;

import com.greem4.springmedicines.dto.ChangePasswordRequest;
import com.greem4.springmedicines.dto.UserResponse;
import com.greem4.springmedicines.service.UserService;
import com.greem4.springmedicines.validation.ChangePassValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ChangePassValidator changePassValidator;

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getProfile(Principal principal) {
        var username = principal.getName();
        return userService.getUserByUsername(username);
    }

    @PutMapping("/changePassword")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request, BindingResult bindingResult) {
        changePassValidator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        userService.changePassword(request.username(), request);
        return ResponseEntity.ok("Пароль успешно изменен");
    }
}
