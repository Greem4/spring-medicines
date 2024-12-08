package com.greem4.springmedicines.http.controller;

import com.greem4.springmedicines.database.entity.Medicine;
import com.greem4.springmedicines.service.MedicineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    public String showMedicines(@RequestParam(defaultValue = "name") String sortBy,
                                @RequestParam(defaultValue = "asc") String sortDirection,
                                Model model) {
        List<Medicine> medicines = medicineService.getAllMedicinesSorted(sortBy, sortDirection);

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
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDirection", sortDirection);
        return "medicinesList";
    }

    @GetMapping("/add")
    public String showAddMedicineForm(Model model) {
        model.addAttribute("medicine", new Medicine());
        return "addMedicineForm";
    }

    @PostMapping("/add")
    public String addMedicine(@Valid @ModelAttribute Medicine medicine, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "addMedicineForm";
        }
        medicineService.addMedicine(medicine);
        return "redirect:/medicines/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditMedicineForm(@PathVariable Long id, Model model) {
        Medicine medicine = medicineService.findById(id);
        model.addAttribute("medicine", medicine);
        return "editMedicineForm";
    }

    @PostMapping("/edit/{id}")
    public String updateMedicine(@PathVariable Long id, @ModelAttribute Medicine medicine) {
        medicineService.updateMedicine(id, medicine);
        return "redirect:/medicines/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
        return "redirect:/medicines/list";
    }
}
