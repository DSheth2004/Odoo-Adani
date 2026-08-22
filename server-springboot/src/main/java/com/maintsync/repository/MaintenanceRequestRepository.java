package com.maintsync.repository;

import com.maintsync.model.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {

    // Find an existing similar request to avoid duplicates for employee submissions
    @Query("SELECT mr FROM MaintenanceRequest mr WHERE (:equipmentId IS NULL OR mr.equipment.id = :equipmentId) AND (:workCenter IS NULL OR mr.workCenter = :workCenter) AND mr.requestedBy.id = :requestedById AND mr.maintenanceType = :maintenanceType AND mr.status = 'New Request'")
    java.util.Optional<MaintenanceRequest> findSimilar(@Param("equipmentId") Long equipmentId,
                                                       @Param("workCenter") String workCenter,
                                                       @Param("requestedById") Long requestedById,
                                                       @Param("maintenanceType") String maintenanceType);

    @Query("SELECT mr FROM MaintenanceRequest mr " +
           "WHERE (:from IS NULL OR COALESCE(mr.scheduledStart, mr.scheduledEnd, mr.createdAt) >= :from) " +
           "AND (:to IS NULL OR COALESCE(mr.scheduledStart, mr.scheduledEnd, mr.createdAt) <= :to) " +
           "AND (:requestedById IS NULL OR mr.requestedBy.id = :requestedById) " +
           "AND (:technicianId IS NULL OR mr.assignedTechnician.id = :technicianId OR (COALESCE(:teamNames, NULL) IS NOT NULL AND mr.teamName IN :teamNames)) " +
           "ORDER BY COALESCE(mr.scheduledStart, mr.scheduledEnd, mr.createdAt) ASC")
    List<MaintenanceRequest> filterRequests(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("requestedById") Long requestedById,
            @Param("technicianId") Long technicianId,
            @Param("teamNames") List<String> teamNames
    );

    @Query("SELECT COUNT(mr) FROM MaintenanceRequest mr WHERE mr.status NOT IN ('Repaired', 'Scrap')")
    long countOpenRequests();

    @Query("SELECT COUNT(mr) FROM MaintenanceRequest mr WHERE mr.status NOT IN ('Repaired', 'Scrap') AND mr.assignedTechnician.id IS NOT NULL")
    long countOpenAssignedRequests();

    @Query("SELECT COUNT(mr) FROM MaintenanceRequest mr " +
           "WHERE mr.status NOT IN ('Repaired', 'Scrap') " +
           "AND (mr.assignedTechnician.id = :technicianId OR (COALESCE(:teamNames, NULL) IS NOT NULL AND mr.teamName IN :teamNames))")
    long countOpenRequestsForTechnician(@Param("technicianId") Long technicianId, @Param("teamNames") List<String> teamNames);

    @Query("SELECT COUNT(mr) FROM MaintenanceRequest mr " +
           "WHERE mr.status NOT IN ('Repaired', 'Scrap') AND mr.assignedTechnician.id = :technicianId")
    long countOpenAssignedRequestsForTechnician(@Param("technicianId") Long technicianId);

    @Query("SELECT mr FROM MaintenanceRequest mr ORDER BY mr.createdAt DESC LIMIT 10")
    List<MaintenanceRequest> findTop10RecentActivities();

    @Query("SELECT mr FROM MaintenanceRequest mr " +
           "WHERE mr.assignedTechnician.id = :technicianId OR (COALESCE(:teamNames, NULL) IS NOT NULL AND mr.teamName IN :teamNames) " +
           "ORDER BY mr.createdAt DESC LIMIT 10")
    List<MaintenanceRequest> findTop10RecentActivitiesForTechnician(
            @Param("technicianId") Long technicianId,
            @Param("teamNames") List<String> teamNames
    );
}
