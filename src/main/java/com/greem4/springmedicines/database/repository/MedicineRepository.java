package com.greem4.springmedicines.database.repository;

import com.greem4.springmedicines.database.entity.Medicine;
import com.greem4.springmedicines.dto.MedicineExpiryNotificationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

// fixme: зачем анноташка?:)
@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    @Query("""
        select new com.greem4.springmedicines.dto.MedicineExpiryNotificationDTO(m.name, m.serialNumber, m.expirationDate)
        from Medicine m
        where m.expirationDate < :date
    """)
    List<MedicineExpiryNotificationDTO> findAllExpiringBefore(LocalDate date);
}
