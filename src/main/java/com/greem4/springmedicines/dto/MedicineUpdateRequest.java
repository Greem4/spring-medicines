package com.greem4.springmedicines.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record MedicineUpdateRequest(

        @NotBlank(message = "Название препарата не может быть пустым")
        String name,

        @NotBlank(message = "Серийный номер не может быть пустым")
        String serialNumber,

        @NotNull(message = "Дата истечения срока годности не может быть пустой")
        @DateTimeFormat(pattern = "dd-MM-yyyy")
        LocalDate expirationDate
) {
}
