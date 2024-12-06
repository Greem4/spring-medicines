package com.greem4.springmedicines.controller;

import com.greem4.springmedicines.database.entity.Medicine;
import com.greem4.springmedicines.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping("/medicines")
    public String showMedicines(Model model) {
        List<Medicine> medicines = medicineService.getAllMedicines();

        List<Map<String, Object>> formatDate = medicines.stream()
                .map(medicine -> {
                    Map<String, Object> medicineMap = new HashMap<>();
                    medicineMap.put("medicine", medicine);
                    if (medicine.getExpirationDate() != null) {
                        String formattedDate = medicineService.formatDate(medicine.getExpirationDate());
                        medicineMap.put("formattedExpirationDate", formattedDate);
                    }
                    return medicineMap;
                })
                .toList();

        model.addAttribute("medicines", formatDate);
        return "medicines";
    }
}
