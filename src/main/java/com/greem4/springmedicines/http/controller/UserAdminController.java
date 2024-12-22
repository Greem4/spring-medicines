package com.greem4.springmedicines.http.controller;

import com.greem4.springmedicines.dto.UserCreatedRequest;
import com.greem4.springmedicines.dto.UserResponse;
import com.greem4.springmedicines.dto.UserRoleUpdateRequest;
import com.greem4.springmedicines.service.UserRoleService;
import com.greem4.springmedicines.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserRoleService userRoleService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody UserCreatedRequest request) {
        if (userService.existsByUsername(request.username())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        return userService.createUser(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userService.getAllUsers(org.springframework.data.domain.PageRequest.of(page, size));
    }

    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PutMapping("/role")
    public ResponseEntity<?> updateUserRole(@RequestBody UserRoleUpdateRequest request) {
        var updated = userRoleService.updateUserRole(request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{username}/enable")
    public ResponseEntity<?> enableUser(@PathVariable String username) {
        var update = userRoleService.enableUser(username);
        return ResponseEntity.ok(update);
    }

    @PutMapping("/{username}/disable")
    public ResponseEntity<?> disableUser(@PathVariable String username) {
        var update = userRoleService.disableUser(username);
        return ResponseEntity.ok(update);
    }

    @GetMapping("/ping")
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "pong";
    }
}
