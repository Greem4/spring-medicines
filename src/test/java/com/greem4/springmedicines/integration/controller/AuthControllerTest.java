package com.greem4.springmedicines.integration.controller;

import com.greem4.springmedicines.dto.JwtResponse;
import com.greem4.springmedicines.dto.LoginRequest;
import com.greem4.springmedicines.dto.RegisterRequest;
import com.greem4.springmedicines.dto.UserResponse;
import com.greem4.springmedicines.domain.Role;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthControllerTest extends IntegrationTestBase {

    @Test
    public void registerTest() {
        var registerRequest = new RegisterRequest("testUser", "password123");

        var response = testRestTemplate.
                postForEntity("/api/v1/auth/register",
                        new HttpEntity<>(registerRequest, getHttpHeaders()),
                        UserResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var userResponse = response.getBody();
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.username()).isEqualTo("testUser");
        assertThat(userResponse.role()).isEqualTo(Role.USER);
        assertThat(userResponse.enabled()).isFalse();
    }

    @Test
    public void userAlreadyExistsTest() {
        RegisterRequest firstRegister = new RegisterRequest("existingUser", "password123");
        RegisterRequest secondRegister = new RegisterRequest("existingUser", "newPassword");

        var firstRequest = new HttpEntity<>(firstRegister, getHttpHeaders());
        var secondRequest = new HttpEntity<>(secondRegister, getHttpHeaders());

        var firstResponse = testRestTemplate.postForEntity("/api/v1/auth/register", firstRequest, UserResponse.class);
        var secondResponse = testRestTemplate.postForEntity("/api/v1/auth/register", secondRequest, String.class);

        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(secondResponse.getBody()).isEqualTo("Пользователь с таким именем уже существует");
    }

    @Test
    public void authenticateUserTest() {
        var loginRequest = new LoginRequest("user", "user");

        var response = testRestTemplate
                .postForEntity("/api/v1/auth/login",
                        new HttpEntity<>(loginRequest, getHttpHeaders()),
                        JwtResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JwtResponse jwtResponse = response.getBody();
        assertThat(jwtResponse).isNotNull();
        assertThat(jwtResponse.type()).isNotEmpty().isEqualTo("Bearer");
        assertThat(jwtResponse.username()).isEqualTo("user");
        assertThat(jwtResponse.role()).isEqualTo(Role.USER);
    }

    @Test
    public void notAuthenticateUserTest() {
        var loginRequest = new LoginRequest("user", "password");

        var response = testRestTemplate
                .postForEntity("/api/v1/auth/login",
                        new HttpEntity<>(loginRequest, getHttpHeaders()),
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Неверные имя пользователя или пароль");
    }

    @Test
    public void logoutTest() {
        var response = testRestTemplate
                .postForEntity("/api/v1/auth/logout",
                        new HttpEntity<>(getHttpHeaders()),
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Logout Successful");
    }
}
