package com.greem4.springmedicines.http.controller;

import com.greem4.springmedicines.dto.MedicineCreateRequest;
import com.greem4.springmedicines.dto.MedicineUpdateRequest;
import com.greem4.springmedicines.dto.MedicineView;
import com.greem4.springmedicines.exception.ResourceNotFoundException;
import com.greem4.springmedicines.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class MedicineRestController {

    private final MedicineService medicineService;

    @GetMapping
    public ResponseEntity<Page<MedicineView>> getAllMedicines(Pageable pageable) {
        Page<MedicineView> medicines = medicineService.getAllMedicines(pageable);
        return ResponseEntity.ok(medicines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineView> getMedicineById(@PathVariable Long id) {
        MedicineView medicine = medicineService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Препарат не найден с ID: " + id));
        return ResponseEntity.ok(medicine);
    }

    @PostMapping
    public ResponseEntity<MedicineView> addMedicine(@Valid @RequestBody MedicineCreateRequest request) {
        MedicineView createdMedicine = medicineService.addMedicine(request);
        return new ResponseEntity<>(createdMedicine, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicineView> updateMedicine(@PathVariable Long id, @Valid @RequestBody MedicineUpdateRequest request) {
        MedicineView updatedMedicine = medicineService.updateMedicine(id, request);
        return ResponseEntity.ok(updatedMedicine);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
        return ResponseEntity.noContent().build();
    }
}
