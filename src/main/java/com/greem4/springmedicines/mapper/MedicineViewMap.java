package com.greem4.springmedicines.mapper;

import com.greem4.springmedicines.domain.Medicine;
import com.greem4.springmedicines.dto.MedicineView;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class MedicineViewMap {
    private static final DateTimeFormatter DATE_FORMATER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static MedicineView toMedicineView(Medicine medicine) {
        return new MedicineView(
                medicine.getId(),
                medicine.getName(),
                medicine.getSerialNumber(),
                formatDate(medicine.getExpirationDate()),
                determineColor(medicine.getExpirationDate())
                );
    }

    private static String determineColor(LocalDate expirationDate) {
        LocalDate today = LocalDate.now();
        Period period = Period.between(today, expirationDate);
        int daysLeft = period.getDays() + period.getMonths() * 30 + period.getYears() * 365;

        if (daysLeft > 90) {
            return "green";
        }
        if (daysLeft > 30) {
            return "orange";
        }
        return "red";
    }

    private static String formatDate(LocalDate localDate) {
        return localDate.format(DATE_FORMATER);
    }
}
