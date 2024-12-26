package com.greem4.springmedicines.dto;

import java.time.LocalDate;

public record MedicineExpiryNotificationDTO(
        String name,
        LocalDate expiryDate
) {
}
