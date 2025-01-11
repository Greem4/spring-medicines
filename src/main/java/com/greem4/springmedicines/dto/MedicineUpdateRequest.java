package com.greem4.springmedicines.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MedicineUpdateRequest(
        @NotBlank() String name,
        @NotBlank() String serialNumber,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        @NotNull() LocalDate expirationDate
) {
}
