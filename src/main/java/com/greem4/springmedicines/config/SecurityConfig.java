package com.greem4.springmedicines.config;

import com.greem4.springmedicines.security.CustomOAuth2SuccessHandler;
import com.greem4.springmedicines.security.OAuth2FailureHandler;
import com.greem4.springmedicines.security.OAuthYandexUserService;
import com.greem4.springmedicines.service.UserService;
import com.greem4.springmedicines.util.security.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Slf4j
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${base.url}")
    private String BASE_URL;

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final OAuth2FailureHandler oAuth2FailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(BASE_URL));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
                    config.setAllowCredentials(true);
                    return config;
                }))

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
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2Yandex()))
                        .successHandler(oAuth2SuccessHandler())
                        .failureHandler(oAuth2FailureHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("Logout Successful");
                        })
                        .permitAll()
                )

                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.debug("Unauthorized API access attempt to: {}", request.getRequestURI());
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                        })
                );

        return http.build();
    }

    @Bean
    public OAuthYandexUserService oAuth2Yandex() {
        return new OAuthYandexUserService(userService);
    }

    @Bean
    public AuthenticationSuccessHandler oAuth2SuccessHandler() {
        return new CustomOAuth2SuccessHandler(jwtUtils);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
            grantedAuthoritiesConverter.setAuthoritiesClaimName("role");
            grantedAuthoritiesConverter.setAuthorityPrefix("");
            return grantedAuthoritiesConverter.convert(jwt);
        });
        return converter;
    }
}
