package com.greem4.springmedicines.integration.service;

import com.greem4.springmedicines.dto.MedicineView;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import com.greem4.springmedicines.service.MedicineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

public class MedicineSortedServiceTest extends IntegrationTestBase {

    @Autowired
    private MedicineService medicineService;

    @Test
    void testGetAllMedicines() {
        var page = medicineService.getAllMedicines(PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(10);
        assertThat(page.getContent())
                .extracting(MedicineView::name)
                .containsExactlyInAnyOrder(
                        "Аспирин",
                        "Парацетамол",
                        "Ибупрофен",
                        "Анальгин",
                        "Кальция глюконат",
                        "Мезатон",
                        "Лоратадин",
                        "Цитрамон",
                        "Мефенамовая кислота",
                        "Нурофен"
                );
    }

    @Test
    void testSortByExpirationDateASC() {
        var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "expirationDate"));
        var page = medicineService.getAllMedicines(pageRequest);

        assertThat(page.getTotalElements()).isEqualTo(10);
        assertThat(page.getContent())
                .extracting(MedicineView::name)
                .containsExactly(
                        "Мефенамовая кислота", // 2024-09-12
                        "Мезатон",              // 2024-10-20
                        "Анальгин",             // 2024-11-30
                        "Аспирин",              // 2024-12-22
                        "Парацетамол",          // 2025-01-15
                        "Ибупрофен",            // 2025-02-10
                        "Кальция глюконат",     // 2025-03-05
                        "Лоратадин",            // 2025-04-18
                        "Цитрамон",             // 2025-05-25
                        "Нурофен"               // 2025-06-30
                );
    }

    @Test
    void testSortByExpirationDateDESC() {
        var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "expirationDate"));
        var page = medicineService.getAllMedicines(pageRequest);

        assertThat(page.getTotalElements()).isEqualTo(10);
        assertThat(page.getContent())
                .extracting(MedicineView::name)
                .containsExactly(
                        "Нурофен",              // 2025-06-30
                        "Цитрамон",             // 2025-05-25
                        "Лоратадин",            // 2025-04-18
                        "Кальция глюконат",     // 2025-03-05
                        "Ибупрофен",            // 2025-02-10
                        "Парацетамол",          // 2025-01-15
                        "Аспирин",              // 2024-12-22
                        "Анальгин",             // 2024-11-30
                        "Мезатон",              // 2024-10-20
                        "Мефенамовая кислота"   // 2024-09-12
                );
    }

    @Test
    void testSortByNameAsc() {
        var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        var page = medicineService.getAllMedicines(pageRequest);

        assertThat(page.getTotalElements()).isEqualTo(10);
        assertThat(page.getContent())
                .extracting(MedicineView::name)
                .containsExactly(
                        "Анальгин",
                        "Аспирин",
                        "Ибупрофен",
                        "Кальция глюконат",
                        "Лоратадин",
                        "Мезатон",
                        "Мефенамовая кислота",
                        "Нурофен",
                        "Парацетамол",
                        "Цитрамон"
                );
    }

    @Test
    void testSortByNameDesc() {
        var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));
        var page = medicineService.getAllMedicines(pageRequest);

        assertThat(page.getTotalElements()).isEqualTo(10);
        assertThat(page.getContent())
                .extracting(MedicineView::name)
                .containsExactly(
                        "Цитрамон",
                        "Парацетамол",
                        "Нурофен",
                        "Мефенамовая кислота",
                        "Мезатон",
                        "Лоратадин",
                        "Кальция глюконат",
                        "Ибупрофен",
                        "Аспирин",
                        "Анальгин"

                );
    }

    @Test
    void testSortByExpirationDateDescAndNameAsc() {
        var pageRequest = PageRequest.of(0, 10, Sort.by(
                Sort.Order.desc("expirationDate"),
                Sort.Order.asc("name")
        ));
        var page = medicineService.getAllMedicines(pageRequest);

        assertThat(page.getTotalElements()).isEqualTo(10);
        assertThat(page.getContent())
                .extracting(MedicineView::name)
                .containsExactly(
                        "Нурофен",              // 2025-06-30
                        "Цитрамон",             // 2025-05-25
                        "Лоратадин",            // 2025-04-18
                        "Кальция глюконат",     // 2025-03-05
                        "Ибупрофен",            // 2025-02-10
                        "Парацетамол",          // 2025-01-15
                        "Аспирин",              // 2024-12-22
                        "Анальгин",             // 2024-11-30
                        "Мезатон",              // 2024-10-20
                        "Мефенамовая кислота"   // 2024-09-12
                );
    }
}
