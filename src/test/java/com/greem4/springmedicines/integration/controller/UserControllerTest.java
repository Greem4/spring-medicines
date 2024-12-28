package com.greem4.springmedicines.integration.controller;

import com.greem4.springmedicines.dto.ChangePasswordRequest;
import com.greem4.springmedicines.dto.UserResponse;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserControllerTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void changePasswordAnotherUser() {
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
}
