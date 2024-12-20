package com.greem4.springmedicines.integration.controller;

import com.greem4.springmedicines.dto.UserRoleUpdateRequest;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserAdminControllerTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate testRestTemplate;

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
}
