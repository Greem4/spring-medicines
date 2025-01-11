package com.greem4.springmedicines.service;

import com.greem4.springmedicines.domain.Medicine;
import com.greem4.springmedicines.repository.MedicineRepository;
import com.greem4.springmedicines.dto.MedicineCreateRequest;
import com.greem4.springmedicines.dto.MedicineUpdateRequest;
import com.greem4.springmedicines.dto.MedicineView;
import com.greem4.springmedicines.exception.ResourceNotFoundException;
import com.greem4.springmedicines.mapper.MedicineViewMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.greem4.springmedicines.mapper.MedicineViewMap.toMedicineView;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public Page<MedicineView> getAllMedicines(Pageable pageable) {
        return medicineRepository.findAll(pageable)
                .map(MedicineViewMap::toMedicineView);
    }

    public Optional<MedicineView> findById(Long id) {
        return medicineRepository.findById(id)
                .map(MedicineViewMap::toMedicineView);
    }

    @Transactional
    public MedicineView addMedicine(MedicineCreateRequest request) {
        var medicine = new Medicine();

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

        var save = medicineRepository.save(existingMedicine);
        return toMedicineView(save);
    }

    @Transactional
    public void deleteMedicine(Long id) {
        var existingMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Препарат не найден"));
        medicineRepository.delete(existingMedicine);
    }
}
