package com.greem4.springmedicines.http.controller;

import com.greem4.springmedicines.database.entity.Medicine;
import com.greem4.springmedicines.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping("/list")
    public String showMedicines(Model model) {
        List<Medicine> medicines = medicineService.getAllMedicines();

        List<Map<String, Object>> formatDate = medicines.stream()
                .map(medicine -> {
                    Map<String, Object> medicineMap = new HashMap<>();
                    medicineMap.put("medicine", medicine);
                    Optional.ofNullable(medicine.getExpirationDate())
                            .ifPresent(date -> medicineMap.put("formattedDate", medicineService.formatDate(date)));
                    medicineMap.put("getColor", medicine.getColor());
                    return medicineMap;
                })
                .toList();

        model.addAttribute("medicines", formatDate);
        return "medicinesList";
    }
}
