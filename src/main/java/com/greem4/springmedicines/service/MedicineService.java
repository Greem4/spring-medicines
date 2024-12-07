package com.greem4.springmedicines.service;

import com.greem4.springmedicines.database.entity.Medicine;
import com.greem4.springmedicines.database.repositort.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public String formatDate(LocalDate localDate) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return localDate.format(format);
    }

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    @Transactional
    public void addMedicine(Medicine medicine) {
        medicineRepository.save(medicine);
    }
}
