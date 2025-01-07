package com.greem4.springmedicines.repository;

import com.greem4.springmedicines.domain.Medicine;
import com.greem4.springmedicines.dto.MedicineExpiryNotificationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    @Query("""
        select new com.greem4.springmedicines.dto.MedicineExpiryNotificationDTO(m.name, m.serialNumber, m.expirationDate)
        from Medicine m
        where m.expirationDate < :date
    """)
    Page<MedicineExpiryNotificationDTO> findAllExpiringBefore(LocalDate date, Pageable pageable);
}
