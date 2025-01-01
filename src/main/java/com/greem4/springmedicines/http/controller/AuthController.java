package com.greem4.springmedicines.http.controller;

import com.greem4.springmedicines.database.entity.User;
import com.greem4.springmedicines.dto.LoginRequest;
import com.greem4.springmedicines.dto.LoginResponse;
import com.greem4.springmedicines.dto.RegisterRequest;
import com.greem4.springmedicines.dto.UserResponse;
import com.greem4.springmedicines.util.security.JwtUtils;
import com.greem4.springmedicines.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        var userResponse = userService.createUser(registerRequest.toUserCreatedRequest());
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username,
                                     @RequestParam String password) {
        log.debug("Attempting to authenticate user: {}", username);

        // 1. Аутентификация через Spring Security
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // Если дошли сюда — логин/пароль корректны
        log.debug("Authentication successful for user: {}", username);

        // 2. Генерим JWT
        String jwt = jwtUtils.generateJwtToken(username);
        log.debug("Generated JWT for user {}: {}", username, jwt);

        // 3. Возвращаем «OAuth2 token response» (имитация)
        Map<String, Object> response = new HashMap<>();
        response.put("access_token", jwt);
        response.put("token_type", "Bearer");
        response.put("expires_in", 3600);
        response.put("scope", "read write");

        log.debug("Returning token response to client for user: {}", username);
        return response;
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout successful");
    }
}
