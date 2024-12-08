package com.greem4.springmedicines.service;

import com.greem4.springmedicines.database.entity.Medicine;
import com.greem4.springmedicines.database.repositort.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public List<Medicine> getAllMedicinesSorted(String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return medicineRepository.findAll(sort);
    }

    public Medicine findById(Long id) {
        return medicineRepository.findById(id).orElse(null);
    }

    @Transactional
    public void addMedicine(Medicine medicine) {
        medicineRepository.save(medicine);
    }

    @Transactional
    public void updateMedicine(Long id, Medicine updatedMedicine) {
        var existingMedicine = medicineRepository.findById(id).orElseThrow();

        var update = Medicine.builder()
                .id(id)
                .name(updatedMedicine.getName())
                .expirationDate(Optional.ofNullable(updatedMedicine
                        .getExpirationDate())
                        .orElse(existingMedicine.getExpirationDate()))
                .serialNumber(updatedMedicine.getSerialNumber())
                .build();

        medicineRepository.save(update);
    }

    @Transactional
    public void deleteMedicine(Long id) {
        medicineRepository.deleteById(id);
    }

    public String formatDate(LocalDate localDate) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return localDate.format(format);
    }
}
