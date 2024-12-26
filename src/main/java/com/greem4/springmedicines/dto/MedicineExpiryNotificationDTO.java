package com.greem4.springmedicines.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record MedicineExpiryNotificationDTO(
        String name,
        String serialNumber,
        @DateTimeFormat(pattern = "dd-MM-yyyy")
        LocalDate expiryDate
) {
}
