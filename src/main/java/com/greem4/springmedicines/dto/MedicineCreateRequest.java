package com.greem4.springmedicines.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MedicineCreateRequest(
        @NotBlank()
        String name,

        @NotBlank()
        String serialNumber,

        @NotNull()
        LocalDate expirationDate
) {
}
