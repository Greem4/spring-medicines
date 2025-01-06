package com.greem4.springmedicines.integration.controller;

import com.greem4.springmedicines.database.entity.Role;
import com.greem4.springmedicines.dto.*;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserAdminControllerTest extends IntegrationTestBase {

    @Test
    void createUserWithoutAuth() {
        var createRequest = new UserCreatedRequest("user2", "123456", Role.USER, true, null, null);

        var response = testRestTemplate
                .postForEntity("/api/v1/admin/users",
                        createRequest,
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void getAllUsersWithoutAuth() {
        var response = testRestTemplate
                .getForEntity("/api/v1/admin/users?page=0&size=10",
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void getUserByUsernameNotFound() {
        var response = testRestTemplate
                .exchange("/api/v1/admin/users/nonExistentUser",
                        HttpMethod.GET,
                        getAuth("admin", "admin"),
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void pingEndpoint() {
        var pingResponse = testRestTemplate
                .exchange("/api/v1/admin/users/ping",
                        HttpMethod.GET,
                        getAuth("admin", "admin"),
                        String.class);

        assertThat(pingResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pingResponse.getBody()).isEqualTo("pong");
    }

    @Test
    void updateUserRoleWithAdminAuth() {
        var request = new UserRoleUpdateRequest("user", "ADMIN");

        var response = testRestTemplate
                .exchange("/api/v1/admin/users/role",
                        HttpMethod.PUT,
                        new HttpEntity<>(request, getHeadersAdmin()),
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void updateUserRoleWithoutAuth() {
        var request = new UserRoleUpdateRequest("user", "ADMIN");
        var response = testRestTemplate
                .exchange("/api/v1/admin/users/role",
                        HttpMethod.PUT,
                        new HttpEntity<>(request),
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void disableUserWithAdminAuth() {
        var response = testRestTemplate
                .exchange("/api/v1/admin/users/user/disable",
                        HttpMethod.PUT,
                        getAuth("admin", "admin"),
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void changePasswordUserAuth() {
        var changePasswordRequest = new ChangePasswordRequest("user", "user", "123456", "123456");

        var response = testRestTemplate
                .exchange("/api/v1/users/changePassword",
                        HttpMethod.PUT,
                        new HttpEntity<>(changePasswordRequest, getHeadersUser()),
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var profileResponse = testRestTemplate
                .exchange("/api/v1/users/profile",
                        HttpMethod.GET,
                        getAuth("user", "123456"),
                        UserResponse.class);

        assertThat(profileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(profileResponse.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(profileResponse.getBody()).username()).isEqualTo("user");
    }
}