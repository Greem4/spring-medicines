package com.greem4.springmedicines.service;

import com.greem4.springmedicines.database.entity.Medicine;
import com.greem4.springmedicines.database.repository.MedicineRepository;
import com.greem4.springmedicines.dto.MedicineCreateRequest;
import com.greem4.springmedicines.dto.MedicineUpdateRequest;
import com.greem4.springmedicines.dto.MedicineView;
import com.greem4.springmedicines.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public Page<MedicineView> getAllMedicines(Pageable pageable) {
        if (!pageable.getSort().isSorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(
                            Sort.Order.asc("name"),
                            Sort.Order.desc("expirationDate"),
                            Sort.Order.desc("serialNumber")
                    )
            );
        }
        return medicineRepository.findAll(pageable)
                .map(this::toMedicineView);
    }

    public Optional<MedicineView> findById(Long id) {
        return medicineRepository.findById(id)
                .map(this::toMedicineView);
    }

    @Transactional
    public MedicineView addMedicine(MedicineCreateRequest request) {
        Medicine medicine = new Medicine();
        medicine.setName(request.name());
        medicine.setSerialNumber(request.serialNumber());
        medicine.setExpirationDate(request.expirationDate());
        var saved = medicineRepository.save(medicine);
        return toMedicineView(saved);
    }

    @Transactional
    public MedicineView updateMedicine(Long id, MedicineUpdateRequest request) {
        var existingMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Препарат не найден"));

        existingMedicine.setName(request.name());
        existingMedicine.setSerialNumber(request.serialNumber());
        existingMedicine.setExpirationDate(request.expirationDate());

        return toMedicineView(existingMedicine);
    }

    @Transactional
    public void deleteMedicine(Long id) {
        var existingMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Препарат не найден"));
        medicineRepository.delete(existingMedicine);
    }

    private String formatDate(LocalDate localDate) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return localDate.format(format);
    }

    private String determineColor(LocalDate expirationDate) {
        LocalDate today = LocalDate.now();
        Period period = Period.between(today, expirationDate);
        int daysLeft = period.getDays() + period.getMonths() * 30 + period.getYears() * 365;

        if (daysLeft > 90) return "green";
        if (daysLeft > 30) return "orange";
        return "red";
    }

    private MedicineView toMedicineView(Medicine medicine) {
        return new MedicineView(
                medicine.getId(),
                medicine.getName(),
                medicine.getSerialNumber(),
                formatDate(medicine.getExpirationDate()),
                determineColor(medicine.getExpirationDate())
        );
    }
}
