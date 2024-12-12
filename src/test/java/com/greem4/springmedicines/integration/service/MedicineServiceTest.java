package com.greem4.springmedicines.integration.service;

import com.greem4.springmedicines.database.repository.MedicineRepository;
import com.greem4.springmedicines.dto.MedicineCreateRequest;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import com.greem4.springmedicines.service.MedicineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class MedicineServiceTest extends IntegrationTestBase {


    @Autowired
    private MedicineService medicineService;

    @Autowired
    private MedicineRepository medicineRepository;

    @Test
    void getAllMedicines() {
        var page = medicineService.getAllMedicines(PageRequest.of(0, 10));
        assertThat(page).isNotEmpty();
        var view = page.getContent().getFirst();
        assertThat(view.name()).isEqualTo("Гепарин");

    }

    @Test
    void addMedicine() {
        var request = new MedicineCreateRequest("TestMed", "TM-001", LocalDate.now().plusDays(30));
        var saveView = medicineService.addMedicine(request);

        var found = medicineService.findById(saveView.id());
        assertThat(found).isPresent();
        assertThat(found.get().name()).isEqualTo("TestMed");
    }

    @Test
    void deleteMedicine() {
        var request = new MedicineCreateRequest("TestMed", "TM-001", LocalDate.now().plusDays(30));
        var saveView = medicineService.addMedicine(request);
        Long id = saveView.id();

        assertThat(medicineService.findById(id)).isPresent();
        medicineService.deleteMedicine(id);
        assertThat(medicineService.findById(id)).isNotPresent();
    }
}