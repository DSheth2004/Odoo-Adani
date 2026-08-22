package com.maintsync.controller;

import com.maintsync.dto.AppDtos.MaintenanceCreateRequest;
import com.maintsync.dto.AppDtos.MaintenanceUpdateRequest;
import com.maintsync.model.Equipment;
import com.maintsync.model.MaintenanceRequest;
import com.maintsync.model.Team;
import com.maintsync.model.User;
import com.maintsync.repository.EquipmentRepository;
import com.maintsync.repository.MaintenanceRequestRepository;
import com.maintsync.repository.TeamRepository;
import com.maintsync.repository.UserRepository;
import com.maintsync.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/requests")
public class MaintenanceRequestController {

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final DashboardService dashboardService;

    public MaintenanceRequestController(
            MaintenanceRequestRepository maintenanceRequestRepository,
            EquipmentRepository equipmentRepository,
            UserRepository userRepository,
            TeamRepository teamRepository,
            DashboardService dashboardService
    ) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<?> getRequests(
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Authentication auth
    ) {
        String role = getRole(auth);
        Long userId = getUserId(auth);

        Long requestedById = "employee".equalsIgnoreCase(role) ? userId : null;
        Long technicianId = "technician".equalsIgnoreCase(role) ? userId : null;
        List<String> teamNames = null;

        if ("technician".equalsIgnoreCase(role) && userId != null) {
            List<Team> teams = teamRepository.findTeamsByMemberUserId(userId);
            teamNames = teams.stream().map(Team::getName).collect(Collectors.toList());
            if (teamNames.isEmpty()) {
                teamNames = null;
            }
        }

        List<MaintenanceRequest> requests = maintenanceRequestRepository.filterRequests(from, to, requestedById, technicianId, teamNames);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRequestById(@PathVariable Long id, Authentication auth) {
        String role = getRole(auth);
        Long userId = getUserId(auth);

        Optional<MaintenanceRequest> reqOpt = maintenanceRequestRepository.findById(id);
        if (reqOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MaintenanceRequest mr = reqOpt.get();

        if ("admin".equalsIgnoreCase(role)) {
            return ResponseEntity.ok(mr);
        }

        if ("employee".equalsIgnoreCase(role)) {
            if (mr.getRequestedBy() != null && mr.getRequestedBy().getId().equals(userId)) {
                return ResponseEntity.ok(mr);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if ("technician".equalsIgnoreCase(role)) {
            if (mr.getAssignedTechnician() != null && mr.getAssignedTechnician().getId().equals(userId)) {
                return ResponseEntity.ok(mr);
            }
            List<Team> teams = teamRepository.findTeamsByMemberUserId(userId);
            boolean inTeam = teams.stream().anyMatch(t -> t.getName().equalsIgnoreCase(mr.getTeamName()));
            if (inTeam) {
                return ResponseEntity.ok(mr);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<?> createRequest(@RequestBody MaintenanceCreateRequest req, Authentication auth) {
        String role = getRole(auth);
        Long userId = getUserId(auth);

        String maintFor = req.getMaintenanceFor() != null ? req.getMaintenanceFor().trim().toLowerCase() : "equipment";
        if ("work center".equalsIgnoreCase(maintFor)) maintFor = "work_center";

        if (!"equipment".equals(maintFor) && !"work_center".equals(maintFor)) {
            return ResponseEntity.badRequest().body(Map.of("error", "maintenance_for must be 'equipment' or 'work_center'"));
        }
        if ("equipment".equals(maintFor) && req.getEquipmentId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "equipment_id is required for maintenance_for=equipment"));
        }
        if ("work_center".equals(maintFor) && (req.getWorkCenter() == null || req.getWorkCenter().isBlank())) {
            return ResponseEntity.badRequest().body(Map.of("error", "work_center is required for maintenance_for=work_center"));
        }
        if (req.getMaintenanceType() == null || req.getMaintenanceType().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "maintenance_type is required"));
        }

        int priority = (req.getPriority() != null) ? req.getPriority() : 3;
        if (priority < 1 || priority > 5) {
            return ResponseEntity.badRequest().body(Map.of("error", "priority must be 1..5"));
        }

        Equipment equipment = null;
        if ("equipment".equals(maintFor) && req.getEquipmentId() != null) {
            equipment = equipmentRepository.findById(req.getEquipmentId()).orElse(null);
        }

        User requestedBy = null;
        if ("employee".equalsIgnoreCase(role)) {
            requestedBy = userRepository.findById(userId).orElse(null);
        } else if (req.getRequestedById() != null) {
            requestedBy = userRepository.findById(req.getRequestedById()).orElse(null);
        }

        User technician = null;
        if (!"employee".equalsIgnoreCase(role) && req.getAssignedTechnicianId() != null) {
            technician = userRepository.findById(req.getAssignedTechnicianId()).orElse(null);
        }

        // Idempotency: check for an existing similar request for the same employee
        if ("employee".equalsIgnoreCase(role)) {
            String workCenterVal = "work_center".equals(maintFor) ? req.getWorkCenter() : null;
            java.util.Optional<com.maintsync.model.MaintenanceRequest> existing = maintenanceRequestRepository.findSimilar(
                    equipment != null ? equipment.getId() : null,
                    workCenterVal,
                    userId,
                    req.getMaintenanceType().trim()
            );
            if (existing.isPresent()) {
                // Return the existing request instead of creating a duplicate
                return ResponseEntity.ok(existing.get());
            }
        }

        MaintenanceRequest mr = MaintenanceRequest.builder()
                .equipment(equipment)
                .requestedBy(requestedBy)
                .assignedTechnician(technician)
                .maintenanceType(req.getMaintenanceType().trim())
                .priority(priority)
                .status(req.getStatus() != null ? req.getStatus().trim() : "New Request")
                .maintenanceFor(maintFor)
                .workCenter("work_center".equals(maintFor) && req.getWorkCenter() != null ? req.getWorkCenter().trim() : null)
                .teamName(req.getTeamName() != null ? req.getTeamName().trim() : null)
                .notes(req.getNotes())
                .instructions(req.getInstructions())
                .scheduledStart(req.getScheduledStart())
                .scheduledEnd(req.getScheduledEnd())
                .build();

        mr = maintenanceRequestRepository.save(mr);
        dashboardService.emitDashboardStats();

        return ResponseEntity.status(HttpStatus.CREATED).body(mr);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<?> updateRequest(@PathVariable Long id, @RequestBody MaintenanceUpdateRequest req, Authentication auth) {
        Optional<MaintenanceRequest> reqOpt = maintenanceRequestRepository.findById(id);
        if (reqOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MaintenanceRequest mr = reqOpt.get();
        String role = getRole(auth);
        Long userId = getUserId(auth);

        if ("technician".equalsIgnoreCase(role)) {
            boolean isAssigned = (mr.getAssignedTechnician() != null && mr.getAssignedTechnician().getId().equals(userId));
            List<Team> teams = teamRepository.findTeamsByMemberUserId(userId);
            final String targetTeamName = mr.getTeamName();
            boolean inTeam = targetTeamName != null && teams.stream().anyMatch(t -> t.getName().equalsIgnoreCase(targetTeamName));

            if (!isAssigned && !inTeam) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (req.getStatus() != null) mr.setStatus(req.getStatus().trim());
            if (req.getScheduledEnd() != null) mr.setScheduledEnd(req.getScheduledEnd());

            mr = maintenanceRequestRepository.save(mr);
            dashboardService.emitDashboardStats();
            return ResponseEntity.ok(mr);
        }

        if (req.getEquipmentId() != null) {
            Equipment eq = equipmentRepository.findById(req.getEquipmentId()).orElse(null);
            mr.setEquipment(eq);
        }
        if (req.getRequestedById() != null) {
            User user = userRepository.findById(req.getRequestedById()).orElse(null);
            mr.setRequestedBy(user);
        }
        if (req.getAssignedTechnicianId() != null) {
            User tech = userRepository.findById(req.getAssignedTechnicianId()).orElse(null);
            mr.setAssignedTechnician(tech);
        }
        if (req.getMaintenanceType() != null) mr.setMaintenanceType(req.getMaintenanceType().trim());
        if (req.getPriority() != null) mr.setPriority(req.getPriority());
        if (req.getStatus() != null) mr.setStatus(req.getStatus().trim());
        if (req.getMaintenanceFor() != null) mr.setMaintenanceFor(req.getMaintenanceFor().trim());
        if (req.getWorkCenter() != null) mr.setWorkCenter(req.getWorkCenter().trim());
        if (req.getTeamName() != null) mr.setTeamName(req.getTeamName().trim());
        if (req.getNotes() != null) mr.setNotes(req.getNotes());
        if (req.getInstructions() != null) mr.setInstructions(req.getInstructions());
        if (req.getScheduledStart() != null) mr.setScheduledStart(req.getScheduledStart());
        if (req.getScheduledEnd() != null) mr.setScheduledEnd(req.getScheduledEnd());

        mr = maintenanceRequestRepository.save(mr);
        dashboardService.emitDashboardStats();
        return ResponseEntity.ok(mr);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRequest(@PathVariable Long id) {
        if (!maintenanceRequestRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        maintenanceRequestRepository.deleteById(id);
        dashboardService.emitDashboardStats();
        return ResponseEntity.ok(Map.of("ok", true));
    }

    private Long getUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) return null;
        try {
            return Long.parseLong(auth.getPrincipal().toString());
        } catch (Exception e) {
            return null;
        }
    }

    private String getRole(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null) return "employee";
        return auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", "").toLowerCase())
                .orElse("employee");
    }
}
