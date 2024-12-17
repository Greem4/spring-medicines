package com.greem4.springmedicines.dto;

public record MedicineView(
        Long id,
        String name,
        String serialNumber,
        String expirationDate,
        String color
) {
}
