package com.greem4.springmedicines.config;

import com.greem4.springmedicines.filter.JwtAuthenticationFilter;
import com.greem4.springmedicines.service.UserService;
import com.greem4.springmedicines.util.security.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@Slf4j
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/medicines/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/changePassword").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/medicines").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/medicines/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/medicines/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2Yandex()))
                        .successHandler(oAuth2SuccessHandler())
                        .failureHandler((req, res, ex) -> {
                            log.debug("Request: {}", req.getRequestURI());
                            log.debug("OAuth2 login failed: {}", ex.getMessage());
                            res.sendRedirect("/?oauth2=error");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )

                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.debug("Unauthorized API access attempt to: {}", request.getRequestURI());
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                        })
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler oAuth2SuccessHandler() {
        // fixme: декомпозиция хромает
        return (request, response, authentication) -> {
            var principal = authentication.getPrincipal();
            log.debug("Registered user: {}", request);
            log.debug("OAuth2SuccessHandler invoked. Principal type: {}", principal.getClass().getName());

            String usernameOrEmail;
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
                usernameOrEmail = userDetails.getUsername();
            } else if (principal instanceof OAuth2User oAuth2User) {
                Object possibleEmail = oAuth2User.getAttributes().get("default_email");
                if (possibleEmail == null) {
                    possibleEmail = oAuth2User.getAttributes().get("email");
                }
                usernameOrEmail = (possibleEmail != null) ? possibleEmail.toString() : authentication.getName();
            } else {
                usernameOrEmail = authentication.getName();
            }

            log.debug("Generating JWT for user/email: {}", usernameOrEmail);
            String jwt = jwtUtils.generateJwtToken(usernameOrEmail);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String json = String.format("{\"token\": \"%s\"}", jwt);
            response.getWriter().write(json);
            response.getWriter().flush();

            log.debug("OAuth2 login success. JWT={} for user={}", jwt, usernameOrEmail);
        };
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2Yandex() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            log.debug("Attributes from Yandex: {}", oAuth2User.getAttributes());

            String provider = "yandex";
            String providerId = oAuth2User.getAttribute("id");
            String email = oAuth2User.getAttribute("default_email");
            if (email == null) {
                email = oAuth2User.getAttribute("email");
            }
            if (providerId == null) {
                throw new OAuth2AuthenticationException("Yandex did not return 'id'");
            }
            if (email == null) {
                throw new OAuth2AuthenticationException("No email from Yandex. Check scope or user settings.");
            }

            log.debug("Trying to find user in DB with provider={}, providerId={}", provider, providerId);
            var userOptional = userService.findByProviderAndProviderId(provider, providerId);

            if (userOptional.isEmpty()) {
                log.debug("User not found. Creating new user with email={}", email);
                userService.saveOAuthUser(email, provider, providerId);
            }

            var userInDb = userService.findByProviderAndProviderId(provider, providerId)
                    .orElseThrow(() -> new OAuth2AuthenticationException("User not found after creation?"));

            String springRole = "ROLE_" + userInDb.getRole();

            return new DefaultOAuth2User(
                    Collections.singletonList(new SimpleGrantedAuthority(springRole)),
                    oAuth2User.getAttributes(),
                    "default_email"
            );
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
