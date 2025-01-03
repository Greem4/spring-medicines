package com.greem4.springmedicines.integration.security;

import com.greem4.springmedicines.dto.MedicineCreateRequest;
import com.greem4.springmedicines.dto.MedicineView;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class MedicineSecurityTest extends IntegrationTestBase {

    @Test
    void getAllWithoutAuth() {
        var response = testRestTemplate
                .exchange("/api/medicines",
                        HttpMethod.GET, null,
                        new ParameterizedTypeReference<>() {
                        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getByIdWithoutAuth() {
        var response = testRestTemplate.
                getForEntity("/api/medicines/1",
                        MedicineView.class);
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);
    }

    @Test
    void postWithoutAuth() {
        var request = new MedicineCreateRequest("TestMedicine", "1222", LocalDate.now().plusMonths(1));
        var response = testRestTemplate
                .postForEntity("/api/medicines",
                        request,
                        String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void postWithUserAuth() {
        var request = new MedicineCreateRequest("TestMedicine", "1222", LocalDate.now().plusMonths(1));
        var response = testRestTemplate
                .exchange("/api/medicines",
                        HttpMethod.POST,
                        new HttpEntity<>(request, getHeadersUser()),
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void postWithAdminAuth() {
        var request = new MedicineCreateRequest("TestMedicine", "1222", LocalDate.now().plusMonths(4));
        var response = testRestTemplate
                .postForEntity("/api/medicines",
                        new HttpEntity<>(request, getHeadersAdmin()),
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void putWithUserTEst() {
        var updateData = Map.of(
                "name", "UpdatedName",
                "serialNumber", "12345",
                "expirationDate", LocalDate.now().minusMonths(1).toString()
        );
        var response = testRestTemplate
                .exchange("/api/medicines",
                        HttpMethod.PUT,
                        new HttpEntity<>(updateData, getHeadersUser()),
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void putWithAuthTest() {
        var updateData = Map.of(
                "name", "UpdatedName",
                "serialNumber", "12345",
                "expirationDate", LocalDate.now().minusMonths(1).toString()
        );

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(updateData, headers);
        var response = testRestTemplate
                .exchange("/api/medicines",
                        HttpMethod.PUT,
                        entity,
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void deleteWithAdminAuthTest() {
        var response = testRestTemplate
                .exchange("/api/medicines/1",
                        HttpMethod.DELETE,
                        getAuth("admin", "admin"),
                        String.class);

        assertThat(response.getStatusCode()).isIn(HttpStatus.NO_CONTENT, HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteWithUserAuthTest() {
        var response = testRestTemplate
                .exchange("/api/medicines/1",
                        HttpMethod.DELETE,
                        getAuth("user", "user"),
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void deleteWithNotAuthTest() {
        var response = testRestTemplate
                .exchange("/api/medicines/1",
                        HttpMethod.DELETE,
                        null,
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
