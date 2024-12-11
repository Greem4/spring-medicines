package com.greem4.springmedicines.dto;

public record MedicineView(
        Long id,
        String name,
        String serialNumber,
        String formattedDate,
        String color
) {
}
