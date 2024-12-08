package com.greem4.springmedicines.database.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "medicines")
public class Medicine extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "serial_number", unique = true, nullable = false)
    private String serialNumber;

    @Column(name = "expiration_date")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate expirationDate;

    public String getColor() {
        return getMonthsUtilExpiration() > 3 ? "green" :
               getMonthsUtilExpiration() >= 2 ? "orange" :
               getMonthsUtilExpiration() >= 1 ? "red" : "black";
    }

    private long getMonthsUtilExpiration() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.minusMonths(1);
        return ChronoUnit.MONTHS.between(firstDayOfMonth, expirationDate);
    }
}