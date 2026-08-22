package com.maintsync.service;

import com.maintsync.dto.AppDtos.DashboardResponse;
import com.maintsync.dto.AppDtos.DashboardStats;
import com.maintsync.dto.AppDtos.MaintenanceRequestSummary;
import com.maintsync.model.MaintenanceRequest;
import com.maintsync.model.Team;
import com.maintsync.model.User;
import com.maintsync.repository.EquipmentRepository;
import com.maintsync.repository.MaintenanceRequestRepository;
import com.maintsync.repository.TeamRepository;
import com.maintsync.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final EquipmentRepository equipmentRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public DashboardService(
            EquipmentRepository equipmentRepository,
            MaintenanceRequestRepository maintenanceRequestRepository,
            UserRepository userRepository,
            TeamRepository teamRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.equipmentRepository = equipmentRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public DashboardStats computeGlobalStats() {
        long criticalEquipment = equipmentRepository.countCriticalEquipment();
        long openRequests = maintenanceRequestRepository.countOpenRequests();
        long techCount = userRepository.countByRole("technician");
        long openAssigned = maintenanceRequestRepository.countOpenAssignedRequests();

        long technicianLoad = techCount > 0 ? Math.min(100, Math.round(((double) openAssigned / techCount) * 100)) : 0;

        return DashboardStats.builder()
                .criticalEquipment(criticalEquipment)
                .technicianLoad(technicianLoad)
                .openRequests(openRequests)
                .build();
    }

    public DashboardResponse getDashboardForUser(User user) {
        if (user != null && "technician".equalsIgnoreCase(user.getRole())) {
            List<Team> teams = teamRepository.findTeamsByMemberUserId(user.getId());
            List<String> teamNames = teams.stream().map(Team::getName).collect(Collectors.toList());
            if (teamNames.isEmpty()) {
                teamNames = null;
            }

            long criticalEquipment = equipmentRepository.countCriticalEquipmentForTechnician(user.getId());
            long openRequests = maintenanceRequestRepository.countOpenRequestsForTechnician(user.getId(), teamNames);
            long openAssigned = maintenanceRequestRepository.countOpenAssignedRequestsForTechnician(user.getId());
            long technicianLoad = openRequests > 0 ? Math.min(100, Math.round(((double) openAssigned / openRequests) * 100)) : 0;

            DashboardStats stats = DashboardStats.builder()
                    .criticalEquipment(criticalEquipment)
                    .technicianLoad(technicianLoad)
                    .openRequests(openRequests)
                    .build();

            List<MaintenanceRequest> activities = maintenanceRequestRepository.findTop10RecentActivitiesForTechnician(user.getId(), teamNames);
            return DashboardResponse.builder()
                    .stats(stats)
                    .activities(mapToSummaries(activities))
                    .build();
        }

        DashboardStats stats = computeGlobalStats();
        List<MaintenanceRequest> activities = maintenanceRequestRepository.findTop10RecentActivities();
        return DashboardResponse.builder()
                .stats(stats)
                .activities(mapToSummaries(activities))
                .build();
    }

    public void emitDashboardStats() {
        try {
            DashboardStats stats = computeGlobalStats();
            messagingTemplate.convertAndSend("/topic/dashboard_stats", stats);
        } catch (Exception ignored) {}
    }

    private List<MaintenanceRequestSummary> mapToSummaries(List<MaintenanceRequest> requests) {
        if (requests == null) return Collections.emptyList();
        return requests.stream().map(r -> MaintenanceRequestSummary.builder()
                .id(r.getId())
                .status(r.getStatus())
                .maintenanceType(r.getMaintenanceType())
                .priority(r.getPriority())
                .createdAt(r.getCreatedAt())
                .maintenanceFor(r.getMaintenanceFor())
                .workCenter(r.getWorkCenter())
                .teamName(r.getTeamName())
                .equipmentName(r.getEquipment() != null ? r.getEquipment().getName() : null)
                .technicianName(r.getAssignedTechnician() != null ? r.getAssignedTechnician().getFullName() : null)
                .build()
        ).collect(Collectors.toList());
    }
}
