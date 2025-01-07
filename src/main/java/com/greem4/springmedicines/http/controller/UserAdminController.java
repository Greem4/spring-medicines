package com.greem4.springmedicines.http.controller;

import com.greem4.springmedicines.domain.User;
import com.greem4.springmedicines.domain.UserAction;
import com.greem4.springmedicines.dto.UserResponse;
import com.greem4.springmedicines.dto.UserRoleUpdateRequest;
import com.greem4.springmedicines.mapper.UserResponseMap;
import com.greem4.springmedicines.service.UserRoleService;
import com.greem4.springmedicines.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserRoleService userRoleService;
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        var users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
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

    @PutMapping("/{username}/{action}")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable String username,
            @PathVariable String action) {
        UserAction userAction = UserAction.fromString(action);
        User updated;

        switch (userAction) {
            case ENABLE -> updated = userRoleService.enableUser(username);
            case DISABLE -> updated = userRoleService.disableUser(username);
            default ->
                    throw new IllegalArgumentException("Unsupported action: " + action);
        }
        var userResponse = UserResponseMap.toUserResponse(updated);

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/ping")
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "pong";
    }
}
