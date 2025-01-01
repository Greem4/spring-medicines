package com.greem4.springmedicines.filter;

import com.greem4.springmedicines.service.CustomUserDetailsService;
import com.greem4.springmedicines.util.security.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        log.debug("Processing request: {}", httpRequest.getRequestURI());

        // 1. Извлекаем заголовок Authorization
        String authHeader = httpRequest.getHeader("Authorization");
        log.debug("Authorization header: {}", authHeader);

        // 2. Если нет заголовка или не начинается с Bearer — пропускаем дальше
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Authorization header is missing or does not start with 'Bearer '. Skipping JWT processing.");
            chain.doFilter(request, response);
            return;
        }

        // 3. Извлекаем сам токен (обрезаем "Bearer ")
        String token = authHeader.substring(7);
        log.debug("Extracted JWT token: {}", token);

        try {
            // 4. Валидируем токен
            boolean isValid = jwtUtils.validateJwtToken(token);
            if (!isValid) {
                log.warn("Invalid JWT token: {}", token);
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
            log.debug("JWT token is valid.");

            // 5. Если токен валиден — получаем username
            String username = jwtUtils.getUsernameFromJwtToken(token);
            log.debug("Extracted username from token: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 6. Загружаем пользователя
                var userDetails = userDetailsService.loadUserByUsername(username);
                log.debug("Loaded user details for username: {}", username);

                // 7. Создаём объект Authentication
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(httpRequest)
                );

                // 8. Кладём в SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("Authentication object created and set in SecurityContext for username: {}", username);
            }
        } catch (Exception ex) {
            log.error("Error processing JWT token: {}", token, ex);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        // 9. Двигаемся дальше по фильтрам
        log.debug("Proceeding with the filter chain.");
        chain.doFilter(request, response);
    }
}
