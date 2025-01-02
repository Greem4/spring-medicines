package com.greem4.springmedicines.integration.controller;

import com.greem4.springmedicines.dto.JwtResponse;
import com.greem4.springmedicines.dto.LoginRequest;
import com.greem4.springmedicines.dto.RegisterRequest;
import com.greem4.springmedicines.dto.UserResponse;
import com.greem4.springmedicines.database.entity.Role;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void registerTest() {
        var registerRequest = new RegisterRequest("testUser", "password123");

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var request = new HttpEntity<>(registerRequest, headers);

        var response = restTemplate.postForEntity("/api/auth/register", request, UserResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var userResponse = response.getBody();
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.username()).isEqualTo("testUser");
        assertThat(userResponse.role()).isEqualTo(Role.USER);
        assertThat(userResponse.enable()).isTrue();
    }

    @Test
    public void userAlreadyExistsTest() {
        RegisterRequest firstRegister = new RegisterRequest("existingUser", "password123");
        RegisterRequest secondRegister = new RegisterRequest("existingUser", "newPassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var firstRequest = new HttpEntity<>(firstRegister, headers);
        var secondRequest = new HttpEntity<>(secondRegister, headers);

        var firstResponse = restTemplate.postForEntity("/api/auth/register", firstRequest, UserResponse.class);
        var secondResponse = restTemplate.postForEntity("/api/auth/register", secondRequest, String.class);

        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(secondResponse.getBody()).isEqualTo("Пользователь с таким именем уже существует");
    }

    @Test
    public void authenticateUserTest() {
        var loginRequest = new LoginRequest("user", "user");

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var request = new HttpEntity<>(loginRequest, headers);

        var response = restTemplate.postForEntity("/api/auth/login", request, JwtResponse.class);

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

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var request = new HttpEntity<>(loginRequest, headers);

        var response = restTemplate.postForEntity("/api/auth/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Неверные имя пользователя или пароль");
    }

    @Test
    public void logoutTest() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var request = new HttpEntity<>(headers);

        var response = restTemplate.postForEntity("/api/auth/logout", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Logout successful");
    }
}
