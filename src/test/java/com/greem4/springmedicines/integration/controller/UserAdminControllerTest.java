package com.greem4.springmedicines.integration.controller;

import com.greem4.springmedicines.database.entity.Role;
import com.greem4.springmedicines.dto.*;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserAdminControllerTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void createUser() {
        var createRequest = new UserCreatedRequest("user1", "123456", Role.USER, true, null, null);

        var auth = getAuth();

        var headers = auth.getHeaders();
        var requestEntity = new HttpEntity<>(createRequest, headers);

        var response = testRestTemplate.exchange("/api/admin/users", HttpMethod.POST, requestEntity, UserResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var createdUser = response.getBody();
        assertThat(createdUser).isNotNull();
        assertThat(Objects.requireNonNull(createdUser).username()).isEqualTo("user1");
        assertThat(createdUser.role()).isEqualTo(Role.USER);
        assertThat(createdUser.enable()).isTrue();

        var getResponse = testRestTemplate.exchange("/api/admin/users/user1", HttpMethod.GET, new HttpEntity<>(headers), UserResponse.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        var body = getResponse.getBody();
        assertThat(body).isNotNull();
        assertThat(Objects.requireNonNull(body).username()).isEqualTo("user1");
        assertThat(body.role()).isEqualTo(Role.USER);
        assertThat(body.enable()).isTrue();
    }


    @Test
    void createUserWithoutAuth() {
        var createRequest = new UserCreatedRequest("user2", "123456", Role.USER, true, null, null);

        var response = testRestTemplate
                .postForEntity("/api/admin/users", createRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void getAllUsersWithoutAuth() {
        var response = testRestTemplate
                .getForEntity("/api/admin/users?page=0&size=10", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void getUserByUsernameNotFound() {
        var response = testRestTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/api/admin/users/nonExistentUser", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void pingEndpoint() {
        var pingEntity = getAuth();

        var pingResponse = testRestTemplate
                .exchange("/api/admin/users/ping", HttpMethod.GET, pingEntity, String.class);

        assertThat(pingResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pingResponse.getBody()).isEqualTo("pong");
    }

    @Test
    void updateUserRoleWithAdminAuth() {
        var request = new UserRoleUpdateRequest("user", "ADMIN");
        var response = testRestTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/api/admin/users/role", HttpMethod.PUT, new HttpEntity<>(request), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void updateUserRoleWithoutAuth() {
        var request = new UserRoleUpdateRequest("user", "ADMIN");
        var response = testRestTemplate
                .exchange("/api/admin/users/role", HttpMethod.PUT, new HttpEntity<>(request), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void disableUserWithAdminAuth() {
        var response = testRestTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/api/admin/users/user/disable", HttpMethod.PUT, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void changePasswordAdminAuth() {
        var changePasswordRequest = new ChangePasswordRequest("user", "123456", "123456");

        var response = testRestTemplate
                .withBasicAuth("user", "user")
                .exchange("/api/users/changePassword",
                        HttpMethod.PUT,
                        new HttpEntity<>(changePasswordRequest),
                        Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var profileResponse = testRestTemplate
                .withBasicAuth("user", "123456")
                .getForEntity("/api/users/profile", UserResponse.class);

        assertThat(profileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(profileResponse.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(profileResponse.getBody()).username()).isEqualTo("user");
    }

    private @NotNull HttpEntity<Object> getAuth() {
        var loginRequest = new LoginRequest("admin", "admin");
        var headersForLogin = new HttpHeaders();
        headersForLogin.setContentType(MediaType.APPLICATION_JSON);
        var loginEntity = new HttpEntity<>(loginRequest, headersForLogin);

        var loginResponse = testRestTemplate
                .postForEntity("/api/auth/login", loginEntity, JwtResponse.class);

        var jwtToken = Objects.requireNonNull(loginResponse.getBody()).token();

        var headersForPing = new HttpHeaders();
        headersForPing.setBearerAuth(jwtToken);
        return new HttpEntity<>(null, headersForPing);
    }
}