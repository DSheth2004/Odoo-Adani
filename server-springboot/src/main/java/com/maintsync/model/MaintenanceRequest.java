package com.maintsync.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_requests")
public class MaintenanceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requested_by_id")
    @JsonProperty("requested_by")
    private User requestedBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_technician_id")
    @JsonProperty("assigned_technician")
    private User assignedTechnician;

    @Column(name = "maintenance_type", nullable = false, length = 50)
    @JsonProperty("maintenance_type")
    private String maintenanceType;

    @Column(name = "priority", nullable = false)
    private Integer priority = 3;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "New Request";

    @Column(name = "maintenance_for", nullable = false, length = 30)
    @JsonProperty("maintenance_for")
    private String maintenanceFor = "equipment";

    @Column(name = "work_center", length = 120)
    @JsonProperty("work_center")
    private String workCenter;

    @Column(name = "team_name", length = 120)
    @JsonProperty("team_name")
    private String teamName;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "scheduled_start")
    @JsonProperty("scheduled_start")
    private LocalDateTime scheduledStart;

    @Column(name = "scheduled_end")
    @JsonProperty("scheduled_end")
    private LocalDateTime scheduledEnd;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public MaintenanceRequest() {}

    public MaintenanceRequest(Long id, Equipment equipment, User requestedBy, User assignedTechnician, String maintenanceType, Integer priority, String status, String maintenanceFor, String workCenter, String teamName, String notes, String instructions, LocalDateTime scheduledStart, LocalDateTime scheduledEnd, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.equipment = equipment;
        this.requestedBy = requestedBy;
        this.assignedTechnician = assignedTechnician;
        this.maintenanceType = maintenanceType;
        this.priority = priority != null ? priority : 3;
        this.status = status != null ? status : "New Request";
        this.maintenanceFor = maintenanceFor != null ? maintenanceFor : "equipment";
        this.workCenter = workCenter;
        this.teamName = teamName;
        this.notes = notes;
        this.instructions = instructions;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MaintenanceRequestBuilder builder() {
        return new MaintenanceRequestBuilder();
    }

    public static class MaintenanceRequestBuilder {
        private Long id;
        private Equipment equipment;
        private User requestedBy;
        private User assignedTechnician;
        private String maintenanceType;
        private Integer priority = 3;
        private String status = "New Request";
        private String maintenanceFor = "equipment";
        private String workCenter;
        private String teamName;
        private String notes;
        private String instructions;
        private LocalDateTime scheduledStart;
        private LocalDateTime scheduledEnd;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public MaintenanceRequestBuilder id(Long id) { this.id = id; return this; }
        public MaintenanceRequestBuilder equipment(Equipment equipment) { this.equipment = equipment; return this; }
        public MaintenanceRequestBuilder requestedBy(User requestedBy) { this.requestedBy = requestedBy; return this; }
        public MaintenanceRequestBuilder assignedTechnician(User assignedTechnician) { this.assignedTechnician = assignedTechnician; return this; }
        public MaintenanceRequestBuilder maintenanceType(String maintenanceType) { this.maintenanceType = maintenanceType; return this; }
        public MaintenanceRequestBuilder priority(Integer priority) { this.priority = priority; return this; }
        public MaintenanceRequestBuilder status(String status) { this.status = status; return this; }
        public MaintenanceRequestBuilder maintenanceFor(String maintenanceFor) { this.maintenanceFor = maintenanceFor; return this; }
        public MaintenanceRequestBuilder workCenter(String workCenter) { this.workCenter = workCenter; return this; }
        public MaintenanceRequestBuilder teamName(String teamName) { this.teamName = teamName; return this; }
        public MaintenanceRequestBuilder notes(String notes) { this.notes = notes; return this; }
        public MaintenanceRequestBuilder instructions(String instructions) { this.instructions = instructions; return this; }
        public MaintenanceRequestBuilder scheduledStart(LocalDateTime scheduledStart) { this.scheduledStart = scheduledStart; return this; }
        public MaintenanceRequestBuilder scheduledEnd(LocalDateTime scheduledEnd) { this.scheduledEnd = scheduledEnd; return this; }
        public MaintenanceRequestBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public MaintenanceRequestBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public MaintenanceRequest build() {
            return new MaintenanceRequest(id, equipment, requestedBy, assignedTechnician, maintenanceType, priority, status, maintenanceFor, workCenter, teamName, notes, instructions, scheduledStart, scheduledEnd, createdAt, updatedAt);
        }
    }

    // Helper getters matching Express backend response contract
    @JsonProperty("equipment_id")
    public Long getEquipmentId() {
        return equipment != null ? equipment.getId() : null;
    }

    @JsonProperty("equipment_name")
    public String getEquipmentName() {
        return equipment != null ? equipment.getName() : null;
    }

    @JsonProperty("requested_by_id")
    public Long getRequestedById() {
        return requestedBy != null ? requestedBy.getId() : null;
    }

    @JsonProperty("requested_by_name")
    public String getRequestedByName() {
        return requestedBy != null ? requestedBy.getFullName() : null;
    }

    @JsonProperty("assigned_technician_id")
    public Long getAssignedTechnicianId() {
        return assignedTechnician != null ? assignedTechnician.getId() : null;
    }

    @JsonProperty("technician_name")
    public String getTechnicianName() {
        return assignedTechnician != null ? assignedTechnician.getFullName() : null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }

    public User getRequestedBy() { return requestedBy; }
    public void setRequestedBy(User requestedBy) { this.requestedBy = requestedBy; }

    public User getAssignedTechnician() { return assignedTechnician; }
    public void setAssignedTechnician(User assignedTechnician) { this.assignedTechnician = assignedTechnician; }

    public String getMaintenanceType() { return maintenanceType; }
    public void setMaintenanceType(String maintenanceType) { this.maintenanceType = maintenanceType; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMaintenanceFor() { return maintenanceFor; }
    public void setMaintenanceFor(String maintenanceFor) { this.maintenanceFor = maintenanceFor; }

    public String getWorkCenter() { return workCenter; }
    public void setWorkCenter(String workCenter) { this.workCenter = workCenter; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public LocalDateTime getScheduledStart() { return scheduledStart; }
    public void setScheduledStart(LocalDateTime scheduledStart) { this.scheduledStart = scheduledStart; }

    public LocalDateTime getScheduledEnd() { return scheduledEnd; }
    public void setScheduledEnd(LocalDateTime scheduledEnd) { this.scheduledEnd = scheduledEnd; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
