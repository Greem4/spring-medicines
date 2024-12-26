package com.greem4.springmedicines.integration.service;

import com.greem4.springmedicines.dto.MedicineCreateRequest;
import com.greem4.springmedicines.dto.MedicineUpdateRequest;
import com.greem4.springmedicines.dto.MedicineView;
import com.greem4.springmedicines.exception.ResourceNotFoundException;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import com.greem4.springmedicines.service.MedicineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class MedicineServiceTest extends IntegrationTestBase {

    @Autowired
    private MedicineService medicineService;

    @Test
    void getAllMedicines() {
        var created1 = new MedicineCreateRequest("Анальгин", "3000", LocalDate.now().plusMonths(1));
        var created2 = new MedicineCreateRequest("Аспирин", "3000", LocalDate.now().plusMonths(1));
        var created3 = new MedicineCreateRequest("Ибупрофен", "3000", LocalDate.now().plusMonths(1));
        medicineService.addMedicine(created1);
        medicineService.addMedicine(created2);
        medicineService.addMedicine(created3);

        var page = medicineService.getAllMedicines(PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(4);

        var medicineNames = page.getContent().stream()
                .map(MedicineView::name)
                .toList();

        assertThat(medicineNames).containsExactlyInAnyOrder("Анальгин", "Аспирин", "Ибупрофен", "тест");
    }

    @Test
    void addMedicine() {
        var request = new MedicineCreateRequest(
                "Aspirin",
                "30121",
                LocalDate.now().plusMonths(1)
        );

        var view = medicineService.addMedicine(request);

        assertThat(view.id()).isNotNull();
        assertThat(view.name()).isEqualTo("Aspirin");
        assertThat(view.serialNumber()).isEqualTo("30121");
        assertThat(view.expirationDate()).isEqualTo(
                LocalDate.now().plusMonths(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        assertThat(view.color()).isEqualTo("red");
    }

    @Test
    void updateMedicine() {
        var request = new MedicineCreateRequest(
                "Aspirin",
                "12345",
                LocalDate.now().plusMonths(5)
        );

        var created = medicineService.addMedicine(request);
        assertThat(created.name()).isEqualTo("Aspirin");

        var update = new MedicineUpdateRequest(
                "Aspirin2",
                "54321",
                LocalDate.now().plusMonths(1)
        );

        medicineService.updateMedicine(created.id(), update);

        assertThat(update.name()).isEqualTo("Aspirin2");
        assertThat(update.serialNumber()).isEqualTo("54321");
        assertThat(update.expirationDate()).isEqualTo(
                LocalDate.now().plusMonths(1)
        );
    }

    @Test
    void deleteMedicines() {
        var deleteMedicine = medicineService.findById(1L);
        assertThat(deleteMedicine).isPresent();

        medicineService.deleteMedicine(deleteMedicine.orElseThrow().id());

        var found = medicineService.findById(deleteMedicine.get().id());

        assertThat(found).isNotPresent();
    }

    @Test
    void testFindById_NotFound() {
        long byId = -1L;

        Optional<MedicineView> found = medicineService.findById(byId);

        assertThat(found).isNotPresent();
    }

    @Test
    void testUpdateMedicine_NotFound() {
        Long nonExistentId = 9999L;
        MedicineUpdateRequest updateRequest = new MedicineUpdateRequest(
                "Non-Existent Medicine",
                "SN00000",
                LocalDate.now().plusMonths(1)
        );

        assertThatThrownBy(() -> medicineService.updateMedicine(nonExistentId, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Препарат не найден");
    }
}