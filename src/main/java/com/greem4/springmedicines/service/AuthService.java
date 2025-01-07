package com.greem4.springmedicines.service;

import com.greem4.springmedicines.dto.JwtResponse;
import com.greem4.springmedicines.security.CustomUserDetails;
import com.greem4.springmedicines.util.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public JwtResponse authenticate(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails.getUsername(), userDetails.getAuthorities());

        return new JwtResponse(
                jwt,
                "Bearer",
                3600,
                "read write",
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getRole()
        );
    }
}

