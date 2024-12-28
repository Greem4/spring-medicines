package com.greem4.springmedicines.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greem4.springmedicines.dto.JwtResponse;
import com.greem4.springmedicines.dto.LoginRequest;
import com.greem4.springmedicines.dto.RegisterRequest;
import com.greem4.springmedicines.dto.UserResponse;
import com.greem4.springmedicines.database.entity.Role;
import com.greem4.springmedicines.database.entity.User;
import com.greem4.springmedicines.database.repository.UserRepository;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegister_Success() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterRequest> request = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<UserResponse> response = restTemplate.postForEntity( "/api/auth/register", request, UserResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserResponse userResponse = response.getBody();
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.username()).isEqualTo("testUser");
        assertThat(userResponse.role()).isEqualTo(Role.USER);
        assertThat(userResponse.enable()).isTrue();

        User user = userRepository.findByUsername("testUser").orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getPassword()).isNotEqualTo("password123"); // Пароль должен быть захеширован
        assertThat(objectMapper.readTree(objectMapper.writeValueAsString(user.getPassword())).isNull()).isFalse();
    }

    @Test
    public void testRegister_UserAlreadyExists() throws Exception {
        // Создание существующего пользователя
        User existingUser = User.builder()
                .username("existingUser")
                .password("password123")
                .role(Role.USER)
                .enabled(true)
                .build();
        userRepository.save(existingUser);

        RegisterRequest registerRequest = new RegisterRequest("existingUser", "newPassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterRequest> request = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/register", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isEqualTo("Пользователь уже существует");
    }

    @Test
    public void testAuthenticateUser_Success() throws Exception {
        // Регистрация пользователя
        User user = User.builder()
                .username("loginUser")
                .password("password123")
                .role(Role.USER)
                .enabled(true)
                .build();
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("loginUser", "password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<JwtResponse> response = restTemplate.postForEntity("/api/auth/login", request, JwtResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JwtResponse jwtResponse = response.getBody();
        assertThat(jwtResponse).isNotNull();
        assertThat(jwtResponse.token()).isNotEmpty();
        assertThat(jwtResponse.type()).isEqualTo("Bearer");
        assertThat(jwtResponse.id()).isEqualTo(user.getId());
        assertThat(jwtResponse.username()).isEqualTo("loginUser");
        assertThat(jwtResponse.role()).isEqualTo("USER");
    }

    @Test
    public void testAuthenticateUser_InvalidCredentials() throws Exception {
        // Регистрация пользователя
        User user = User.builder()
                .username("invalidUser")
                .password("password123")
                .role(Role.USER)
                .enabled(true)
                .build();
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("invalidUser", "wrongPassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Неверные учетные данные");
    }

    @Test
    public void testLogout_Success() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/logout", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Logout successful");
    }
}
