package com.greem4.springmedicines.database.repositort;

import com.greem4.springmedicines.database.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

}
