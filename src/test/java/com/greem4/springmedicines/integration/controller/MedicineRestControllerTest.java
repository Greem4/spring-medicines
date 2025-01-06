package com.greem4.springmedicines.integration.controller;

import com.greem4.springmedicines.dto.MedicineCreateRequest;
import com.greem4.springmedicines.dto.MedicineUpdateRequest;
import com.greem4.springmedicines.dto.MedicineView;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class MedicineRestControllerTest extends IntegrationTestBase {

    @Test
    void getAllMedicines() {
        var requests = Arrays.asList(
                new MedicineCreateRequest("Аспирин", "3000", LocalDate.now().plusMonths(3)),
                new MedicineCreateRequest("Парацетамол", "2000", LocalDate.now().plusMonths(6)),
                new MedicineCreateRequest("Ибупрофен", "1500", LocalDate.now().plusMonths(2))
        );

        for (MedicineCreateRequest request : requests) {
            var response = createMedicine(request);

            assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.CREATED);
        }

        ResponseEntity<PagedModel<EntityModel<MedicineView>>> response = testRestTemplate
                .exchange("/api/medicines",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = response.getBody();
        assertThat(body).isNotNull();
        var medicines = body.getContent();
        assertThat(medicines).hasSizeGreaterThanOrEqualTo(3);
        assertThat(medicines.stream().map(m -> Objects.requireNonNull(m.getContent()).name()))
                .contains("Аспирин", "Парацетамол", "Ибупрофен");
    }

    @Test
    void getMedicineById() {
        var request = new MedicineCreateRequest("Аспирин", "3000", LocalDate.now().plusMonths(3));

        createMedicine(request);

        var response = testRestTemplate
                .getForEntity("/api/medicines/2",
                        MedicineView.class);

        assertThat(testRestTemplate.getForEntity("/api/medicines/2", MedicineView.class).getStatusCode()).isEqualTo(HttpStatus.OK);
        var medicine = response.getBody();
        assertThat(medicine).isNotNull();
        assertThat(medicine.name()).isEqualTo("Аспирин");
        assertThat(medicine.serialNumber()).isEqualTo("3000");
    }

    @Test
    void addMedicine() {
        var request = new MedicineCreateRequest("Ибупрофен", "3000", LocalDate.now().plusMonths(3));
        var response = createMedicine(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var created = response.getBody();
        assertThat(created).isNotNull();
        assertThat(created.id()).isNotNull();
        assertThat(created.name()).isEqualTo("Ибупрофен");
        assertThat(created.serialNumber()).isEqualTo("3000");
    }

    @Test
    void updateMedicine() {
        var request = new MedicineUpdateRequest("Новый аспирин", "5000", LocalDate.now().plusWeeks(2));

        var response = testRestTemplate
                .exchange("/api/medicines/1",
                        HttpMethod.PUT,
                        getHttpEntity(request),
                        MedicineView.class);

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
                .exchange("/api/medicines/1",
                        HttpMethod.DELETE,
                        getAuth("admin", "admin"),
                        Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var notFoundResponse = testRestTemplate.getForEntity("/api/medicines/1", String.class);
        assertThat(notFoundResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getMedicineByIdNotFound() {
        var response = testRestTemplate.getForEntity("/api/medicines/999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateMedicineNotFound() {
        var updateRequest = new MedicineUpdateRequest("Новый аспирин", "9999", LocalDate.now().plusMonths(1));

        var response = testRestTemplate
                .exchange("/api/medicines/999",
                        HttpMethod.PUT,
                        getHttpEntity(updateRequest),
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteMedicineNotFound() {
        var response = testRestTemplate
                .exchange("/api/medicines/999",
                        HttpMethod.DELETE,
                        getAuth("admin", "admin"),
                        String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private @NotNull HttpEntity<MedicineUpdateRequest> getHttpEntity(MedicineUpdateRequest updateRequest) {
        var headers = getHeadersAdmin();
        return new HttpEntity<>(updateRequest, headers);
    }

    private ResponseEntity<MedicineView> createMedicine(MedicineCreateRequest request) {
        return testRestTemplate
                .exchange("/api/medicines",
                        HttpMethod.POST,
                        new HttpEntity<>(request, getHeadersAdmin()),
                        MedicineView.class);
    }
}