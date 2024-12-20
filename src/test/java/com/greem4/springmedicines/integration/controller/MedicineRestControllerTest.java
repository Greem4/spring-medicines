package com.greem4.springmedicines.integration.controller;

import com.greem4.springmedicines.dto.MedicineCreateRequest;
import com.greem4.springmedicines.dto.MedicineUpdateRequest;
import com.greem4.springmedicines.dto.MedicineView;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class MedicineRestControllerTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void getAllMedicines() {
        ResponseEntity<PagedModel<EntityModel<MedicineView>>> response = testRestTemplate
                .exchange("/api/medicines", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = response.getBody();
        assertThat(body).isNotNull();
        var medicines = body.getContent();
        assertThat(medicines).hasSizeGreaterThanOrEqualTo(2);
        assertThat(medicines.stream().map(m -> Objects.requireNonNull(m.getContent()).name()))
                .contains("Аспирин", "Парацетамол");
    }

    @Test
    void getMedicineById() {
        var response = testRestTemplate
                .getForEntity("/api/medicines/1", MedicineView.class);

        assertThat(testRestTemplate.getForEntity("/api/medicines/1", MedicineView.class).getStatusCode()).isEqualTo(HttpStatus.OK);
        var medicine = response.getBody();
        assertThat(medicine).isNotNull();
        assertThat(medicine.name()).isEqualTo("Аспирин");
        assertThat(medicine.serialNumber()).isEqualTo("SN101");
    }

    @Test
    void addMedicine() {
        var request = new MedicineCreateRequest("Ибупрофен", "3000", LocalDate.now().plusMonths(3));
        var response = testRestTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("/api/medicines", request, MedicineView.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var created = response.getBody();
        assertThat(created).isNotNull();
        assertThat(created.id()).isNotNull();
        assertThat(created.name()).isEqualTo("Ибупрофен");
        assertThat(created.serialNumber()).isEqualTo("3000");
    }

    @Test
    void updateMedicine() {
        var updateRequest = new MedicineUpdateRequest("Новый аспирин", "5000", LocalDate.now().plusWeeks(2));
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(updateRequest, headers);

        var response = testRestTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/api/medicines/1",
                        HttpMethod.PUT, entity, MedicineView.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var updated = response.getBody();
        assertThat(updated).isNotNull();
        assertThat(updated.name()).isEqualTo("Новый аспирин");
        assertThat(updated.serialNumber()).isEqualTo("5000");
        var expectedDate = LocalDate.now().plusWeeks(2).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        assertThat(updated.expirationDate()).isEqualTo(expectedDate);
    }

    @Test
    void deleteMedicine() {
        var response = testRestTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/api/medicines/2", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var notFoundResponse = testRestTemplate.getForEntity("/api/medicines/2", String.class);
        assertThat(notFoundResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetMedicineByIdNotFound() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/medicines/999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testUpdateMedicineNotFound() {
        var updateRequest = new MedicineUpdateRequest("Новый аспирин", "9999", LocalDate.now().plusMonths(1));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(updateRequest, headers);

        var response = testRestTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/api/medicines/999",
                        HttpMethod.PUT, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteMedicineNotFound() {
        var response = testRestTemplate
                .withBasicAuth("admin", "admin")
                .exchange("/api/medicines/999", HttpMethod.DELETE, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}