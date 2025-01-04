package com.greem4.springmedicines.database.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// fixme: и билдер, и сеттер - странная практика. Тут или крестик, или штаны
@Builder
@Entity
// fixme: таблицы обычно в ед.ч. именуют
@Table(name = "medicines")
public class Medicine extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // fixme: не помню уникального индекса в миграциях под это
    @Column(name = "serial_number", unique = true, nullable = false)
    private String serialNumber;

    @Column(name = "expiration_date")
    // fixme: зачем?
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate expirationDate;

}
