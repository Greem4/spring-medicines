package com.greem4.springmedicines.http.controller;

import com.greem4.springmedicines.dto.UserRoleUpdateRequest;
import com.greem4.springmedicines.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserRoleService userRoleService;

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
}
