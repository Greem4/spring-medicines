package com.greem4.springmedicines.integration.controller;

import com.greem4.springmedicines.dto.ChangePasswordRequest;
import com.greem4.springmedicines.dto.UserResponse;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserControllerTest extends IntegrationTestBase {

    @Test
    void changePasswordAnotherUser() {
        var changePasswordRequest = new ChangePasswordRequest("user", "user",  "123456", "123456");

        var response = testRestTemplate
                .exchange("/api/v1/users/changePassword",
                        HttpMethod.PUT,
                        new HttpEntity<>(changePasswordRequest, getHeadersUser()),
                        Void.class);

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
