package com.greem4.springmedicines.http.controller;

import com.greem4.springmedicines.dto.JwtResponse;
import com.greem4.springmedicines.dto.LoginRequest;
import com.greem4.springmedicines.dto.RegisterRequest;
import com.greem4.springmedicines.dto.UserResponse;
import com.greem4.springmedicines.security.CustomUserDetails;
import com.greem4.springmedicines.service.UserService;
import com.greem4.springmedicines.util.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("checkstyle:RegexpMultiline")
@Slf4j
@RestController
// fixme: если стремимся к REST API - как минимум не хватает версионирования в пути.
//  А заодно еще и разделения на публичный/приватный АПИ, опционально
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
    public ResponseEntity<JwtResponse> authenticateUser(
            @RequestBody @Valid LoginRequest loginRequest
    ) {
        log.debug("Attempting to authenticate user: {}", loginRequest.username());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );

        // fixme: это вполне умеют делать секьюрити фильтры:) вовсе не обязательно вручную сетать секьюрити контекст
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails.getUsername());
        log.debug("Generated JWT for user {}: {}", userDetails.getAuthorities(), jwt);

        var jwtResponse = new JwtResponse(
                jwt,
                "Bearer",
                3600,
                "read write",
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getRole()
        );
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
// fixme: а как же глобал логаут?)
        return ResponseEntity.ok("Logout successful");
    }
}
