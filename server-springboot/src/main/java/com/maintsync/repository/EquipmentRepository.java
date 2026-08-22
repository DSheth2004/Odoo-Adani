package com.maintsync.repository;

import com.maintsync.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Optional<Equipment> findBySerialNumber(String serialNumber);
    boolean existsBySerialNumber(String serialNumber);

    @Query("SELECT e FROM Equipment e " +
           "WHERE (:employeeId IS NULL OR e.employee.id = :employeeId) " +
           "AND (:q IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "     OR LOWER(e.serialNumber) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "     OR LOWER(COALESCE(e.category, '')) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "     OR LOWER(COALESCE(e.company, '')) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "ORDER BY e.id DESC")
    List<Equipment> searchEquipment(@Param("employeeId") Long employeeId, @Param("q") String q);

    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.status IN ('Down', 'Critical')")
    long countCriticalEquipment();

    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.technician.id = :technicianId AND e.status IN ('Down', 'Critical')")
    long countCriticalEquipmentForTechnician(@Param("technicianId") Long technicianId);
}
