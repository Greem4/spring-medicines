package com.greem4.springmedicines.http.controller;

import com.greem4.springmedicines.dto.*;
import com.greem4.springmedicines.security.CustomUserDetails;
import com.greem4.springmedicines.service.UserService;
import com.greem4.springmedicines.util.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        var userResponse = userService.createUser(request);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
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
