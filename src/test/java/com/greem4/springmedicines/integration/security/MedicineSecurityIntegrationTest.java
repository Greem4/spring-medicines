package com.greem4.springmedicines.integration.security;

import com.greem4.springmedicines.dto.MedicineCreateRequest;
import com.greem4.springmedicines.dto.MedicineView;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class MedicineSecurityIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void testGetAllWithoutAuth() {
        var response = testRestTemplate.getForEntity("/api/medicines", PagedModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testGetByIdWithoutAuth() {
        var response = testRestTemplate.getForEntity("/api/medicines/1", MedicineView.class);
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);
    }

    @Test
    void testPostWithoutAuth() {
        var request = new MedicineCreateRequest("TestMedicine", "1222", LocalDate.now().plusMonths(1));
        var response = testRestTemplate.postForEntity("/api/medicines", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testPostWithUserAuth() {
        var request = new MedicineCreateRequest("TestMedicine", "1222", LocalDate.now().plusMonths(1));
        var response = testRestTemplate
                .withBasicAuth("user", "user")
                .postForEntity("/api/medicines", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testPostWithAdminAuth() {
        var request = new MedicineCreateRequest("TestMedicine", "1222", LocalDate.now().plusMonths(4));
        var response = testRestTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("/api/medicines", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testPutWithUser() {
        var updateData = Map.of(
                "name", "UpdatedName",
                "serialNumber", "12345",
                "expirationDate", LocalDate.now().minusMonths(1).toString()
        );

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(updateData, headers);

        var response = testRestTemplate
                .withBasicAuth("user", "user")
                .exchange("/api/medicines", HttpMethod.PUT, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testPutWithAuth() {
        var updateData = Map.of(
                "name", "UpdatedName",
                "serialNumber", "12345",
                "expirationDate", LocalDate.now().minusMonths(1).toString()
        );

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(updateData, headers);
        var response = testRestTemplate
                .exchange("/api/medicines", HttpMethod.PUT, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testDeleteWithAdmin() {
        var response = testRestTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/api/medicines/1", HttpMethod.DELETE, null, String.class);

        assertThat(response.getStatusCode()).isIn(HttpStatus.NO_CONTENT, HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteWithUser() {
        var response = testRestTemplate
                .withBasicAuth("user", "user")
                .exchange("/api/medicines/1", HttpMethod.DELETE, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testDeleteWithAuth() {
        var response = testRestTemplate
                .exchange("/api/medicines/1", HttpMethod.DELETE, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
