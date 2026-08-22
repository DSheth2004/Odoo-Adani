package com.maintsync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public class AppDtos {

    public static class RoleUpdateRequest {
        private String role;
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class TeamCreateRequest {
        private String name;
        private String company;
        @JsonProperty("member_user_id")
        private Long memberUserId;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
        public Long getMemberUserId() { return memberUserId; }
        public void setMemberUserId(Long memberUserId) { this.memberUserId = memberUserId; }
    }

    public static class AddTeamMemberRequest {
        @JsonProperty("user_id")
        private Long userId;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }

    public static class EquipmentRequest {
        private String name;
        @JsonProperty("employee_id")
        private Long employeeId;
        private String department;
        @JsonProperty("serial_number")
        private String serialNumber;
        @JsonProperty("technician_id")
        private Long technicianId;
        private String category;
        private String company;
        private String status;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Long getEmployeeId() { return employeeId; }
        public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getSerialNumber() { return serialNumber; }
        public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
        public Long getTechnicianId() { return technicianId; }
        public void setTechnicianId(Long technicianId) { this.technicianId = technicianId; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class MaintenanceCreateRequest {
        @JsonProperty("equipment_id")
        private Long equipmentId;
        @JsonProperty("requested_by_id")
        private Long requestedById;
        @JsonProperty("assigned_technician_id")
        private Long assignedTechnicianId;
        @JsonProperty("maintenance_type")
        private String maintenanceType;
        private Integer priority;
        private String status;
        @JsonProperty("maintenance_for")
        private String maintenanceFor;
        @JsonProperty("work_center")
        private String workCenter;
        @JsonProperty("team_name")
        private String teamName;
        private String notes;
        private String instructions;
        @JsonProperty("scheduled_start")
        private LocalDateTime scheduledStart;
        @JsonProperty("scheduled_end")
        private LocalDateTime scheduledEnd;

        public Long getEquipmentId() { return equipmentId; }
        public void setEquipmentId(Long equipmentId) { this.equipmentId = equipmentId; }
        public Long getRequestedById() { return requestedById; }
        public void setRequestedById(Long requestedById) { this.requestedById = requestedById; }
        public Long getAssignedTechnicianId() { return assignedTechnicianId; }
        public void setAssignedTechnicianId(Long assignedTechnicianId) { this.assignedTechnicianId = assignedTechnicianId; }
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
    }

    public static class MaintenanceUpdateRequest {
        @JsonProperty("equipment_id")
        private Long equipmentId;
        @JsonProperty("requested_by_id")
        private Long requestedById;
        @JsonProperty("assigned_technician_id")
        private Long assignedTechnicianId;
        @JsonProperty("maintenance_type")
        private String maintenanceType;
        private Integer priority;
        private String status;
        @JsonProperty("maintenance_for")
        private String maintenanceFor;
        @JsonProperty("work_center")
        private String workCenter;
        @JsonProperty("team_name")
        private String teamName;
        private String notes;
        private String instructions;
        @JsonProperty("scheduled_start")
        private LocalDateTime scheduledStart;
        @JsonProperty("scheduled_end")
        private LocalDateTime scheduledEnd;

        public Long getEquipmentId() { return equipmentId; }
        public void setEquipmentId(Long equipmentId) { this.equipmentId = equipmentId; }
        public Long getRequestedById() { return requestedById; }
        public void setRequestedById(Long requestedById) { this.requestedById = requestedById; }
        public Long getAssignedTechnicianId() { return assignedTechnicianId; }
        public void setAssignedTechnicianId(Long assignedTechnicianId) { this.assignedTechnicianId = assignedTechnicianId; }
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
    }

    public static class DashboardStats {
        private long criticalEquipment;
        private long technicianLoad;
        private long openRequests;

        public DashboardStats() {}
        public DashboardStats(long criticalEquipment, long technicianLoad, long openRequests) {
            this.criticalEquipment = criticalEquipment;
            this.technicianLoad = technicianLoad;
            this.openRequests = openRequests;
        }

        public static DashboardStatsBuilder builder() {
            return new DashboardStatsBuilder();
        }

        public static class DashboardStatsBuilder {
            private long criticalEquipment;
            private long technicianLoad;
            private long openRequests;

            public DashboardStatsBuilder criticalEquipment(long criticalEquipment) { this.criticalEquipment = criticalEquipment; return this; }
            public DashboardStatsBuilder technicianLoad(long technicianLoad) { this.technicianLoad = technicianLoad; return this; }
            public DashboardStatsBuilder openRequests(long openRequests) { this.openRequests = openRequests; return this; }

            public DashboardStats build() {
                return new DashboardStats(criticalEquipment, technicianLoad, openRequests);
            }
        }

        public long getCriticalEquipment() { return criticalEquipment; }
        public void setCriticalEquipment(long criticalEquipment) { this.criticalEquipment = criticalEquipment; }
        public long getTechnicianLoad() { return technicianLoad; }
        public void setTechnicianLoad(long technicianLoad) { this.technicianLoad = technicianLoad; }
        public long getOpenRequests() { return openRequests; }
        public void setOpenRequests(long openRequests) { this.openRequests = openRequests; }
    }

    public static class DashboardResponse {
        private DashboardStats stats;
        private List<MaintenanceRequestSummary> activities;

        public DashboardResponse() {}
        public DashboardResponse(DashboardStats stats, List<MaintenanceRequestSummary> activities) {
            this.stats = stats;
            this.activities = activities;
        }

        public static DashboardResponseBuilder builder() {
            return new DashboardResponseBuilder();
        }

        public static class DashboardResponseBuilder {
            private DashboardStats stats;
            private List<MaintenanceRequestSummary> activities;

            public DashboardResponseBuilder stats(DashboardStats stats) { this.stats = stats; return this; }
            public DashboardResponseBuilder activities(List<MaintenanceRequestSummary> activities) { this.activities = activities; return this; }

            public DashboardResponse build() {
                return new DashboardResponse(stats, activities);
            }
        }

        public DashboardStats getStats() { return stats; }
        public void setStats(DashboardStats stats) { this.stats = stats; }
        public List<MaintenanceRequestSummary> getActivities() { return activities; }
        public void setActivities(List<MaintenanceRequestSummary> activities) { this.activities = activities; }
    }

    public static class MaintenanceRequestSummary {
        private Long id;
        private String status;
        @JsonProperty("maintenance_type")
        private String maintenanceType;
        private Integer priority;
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
        @JsonProperty("maintenance_for")
        private String maintenanceFor;
        @JsonProperty("work_center")
        private String workCenter;
        @JsonProperty("team_name")
        private String teamName;
        @JsonProperty("equipment_name")
        private String equipmentName;
        @JsonProperty("technician_name")
        private String technicianName;

        public MaintenanceRequestSummary() {}
        public MaintenanceRequestSummary(Long id, String status, String maintenanceType, Integer priority, LocalDateTime createdAt, String maintenanceFor, String workCenter, String teamName, String equipmentName, String technicianName) {
            this.id = id;
            this.status = status;
            this.maintenanceType = maintenanceType;
            this.priority = priority;
            this.createdAt = createdAt;
            this.maintenanceFor = maintenanceFor;
            this.workCenter = workCenter;
            this.teamName = teamName;
            this.equipmentName = equipmentName;
            this.technicianName = technicianName;
        }

        public static MaintenanceRequestSummaryBuilder builder() {
            return new MaintenanceRequestSummaryBuilder();
        }

        public static class MaintenanceRequestSummaryBuilder {
            private Long id;
            private String status;
            private String maintenanceType;
            private Integer priority;
            private LocalDateTime createdAt;
            private String maintenanceFor;
            private String workCenter;
            private String teamName;
            private String equipmentName;
            private String technicianName;

            public MaintenanceRequestSummaryBuilder id(Long id) { this.id = id; return this; }
            public MaintenanceRequestSummaryBuilder status(String status) { this.status = status; return this; }
            public MaintenanceRequestSummaryBuilder maintenanceType(String maintenanceType) { this.maintenanceType = maintenanceType; return this; }
            public MaintenanceRequestSummaryBuilder priority(Integer priority) { this.priority = priority; return this; }
            public MaintenanceRequestSummaryBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
            public MaintenanceRequestSummaryBuilder maintenanceFor(String maintenanceFor) { this.maintenanceFor = maintenanceFor; return this; }
            public MaintenanceRequestSummaryBuilder workCenter(String workCenter) { this.workCenter = workCenter; return this; }
            public MaintenanceRequestSummaryBuilder teamName(String teamName) { this.teamName = teamName; return this; }
            public MaintenanceRequestSummaryBuilder equipmentName(String equipmentName) { this.equipmentName = equipmentName; return this; }
            public MaintenanceRequestSummaryBuilder technicianName(String technicianName) { this.technicianName = technicianName; return this; }

            public MaintenanceRequestSummary build() {
                return new MaintenanceRequestSummary(id, status, maintenanceType, priority, createdAt, maintenanceFor, workCenter, teamName, equipmentName, technicianName);
            }
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMaintenanceType() { return maintenanceType; }
        public void setMaintenanceType(String maintenanceType) { this.maintenanceType = maintenanceType; }
        public Integer getPriority() { return priority; }
        public void setPriority(Integer priority) { this.priority = priority; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public String getMaintenanceFor() { return maintenanceFor; }
        public void setMaintenanceFor(String maintenanceFor) { this.maintenanceFor = maintenanceFor; }
        public String getWorkCenter() { return workCenter; }
        public void setWorkCenter(String workCenter) { this.workCenter = workCenter; }
        public String getTeamName() { return teamName; }
        public void setTeamName(String teamName) { this.teamName = teamName; }
        public String getEquipmentName() { return equipmentName; }
        public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
        public String getTechnicianName() { return technicianName; }
        public void setTechnicianName(String technicianName) { this.technicianName = technicianName; }
    }
}
