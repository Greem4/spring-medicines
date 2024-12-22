package com.greem4.springmedicines.integration.controller;

import com.greem4.springmedicines.database.entity.Role;
import com.greem4.springmedicines.dto.UserCreatedRequest;
import com.greem4.springmedicines.dto.UserResponse;
import com.greem4.springmedicines.dto.UserRoleUpdateRequest;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserAdminControllerTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void createUser() {
        var createRequest = new UserCreatedRequest("user1", "123", Role.USER, true);

        var response = testRestTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("/api/admin/users", createRequest, UserResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var createdUser = response.getBody();
        assertThat(createdUser).isNotNull();
        assertThat(Objects.requireNonNull(createdUser).username()).isEqualTo("user1");
        assertThat(createdUser.role()).isEqualTo(Role.USER);
        assertThat(createdUser.enable()).isTrue();

        var getResponse = testRestTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/api/admin/users/user1", UserResponse.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        var body = getResponse.getBody();
        assertThat(body).isNotNull();
        assertThat(Objects.requireNonNull(body).username()).isEqualTo("user1");
        assertThat(body.role()).isEqualTo(Role.USER);
        assertThat(body.enable()).isTrue();
    }

    @Test
    void createUserWithoutAuth() {
        var createRequest = new UserCreatedRequest("user2", "123", Role.USER, true);

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
        var response = testRestTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/api/admin/users/ping", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getBody()).isEqualTo("pong");
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

//    @Test
//    void changePassword
}
