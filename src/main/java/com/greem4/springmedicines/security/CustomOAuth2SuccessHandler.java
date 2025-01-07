package com.greem4.springmedicines.security;

import com.greem4.springmedicines.util.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        logPrincipalInfo(request, authentication);

        var usernameOrEmail = extractUsernameOrEmail(authentication);
        log.debug("Generating JWT for user/email: {}", usernameOrEmail);

        var jwt = jwtUtils.generateJwtToken(usernameOrEmail);

        writeJsonResponse(response, jwt);
        log.debug("OAuth2 login success. JWT={} for user={}", jwt, usernameOrEmail);
    }

    private void logPrincipalInfo(HttpServletRequest request, Authentication authentication) {
        var principal = authentication.getPrincipal();
        log.debug("Registered user: {}", request);
        log.debug("OAuth2SuccessHandler invoked. Principal type: {}", principal.getClass().getName());
    }

    private String extractUsernameOrEmail(Authentication authentication) {
        var principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        if (principal instanceof OAuth2User oauth2User) {
            var possibleEmail = oauth2User.getAttributes().get("default_email");
            if (possibleEmail == null) {
                possibleEmail = oauth2User.getAttributes().get("email");
            }
            return (possibleEmail != null) ? possibleEmail.toString() : authentication.getName();
        }
        return authentication.getName();
    }

    private void writeJsonResponse(HttpServletResponse response, String jwt) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        var json = String.format("{\"token\": \"%s\"}", jwt);
        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
