package com.maintsync.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    @JsonProperty("employee")
    private User employee;

    @Column(name = "department", length = 80)
    private String department;

    @Column(name = "serial_number", nullable = false, unique = true, length = 60)
    @JsonProperty("serial_number")
    private String serialNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "technician_id")
    @JsonProperty("technician")
    private User technician;

    @Column(name = "category", length = 80)
    private String category;

    @Column(name = "company", length = 120)
    private String company;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "Active";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public Equipment() {}

    public Equipment(Long id, String name, User employee, String department, String serialNumber, User technician, String category, String company, String status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.employee = employee;
        this.department = department;
        this.serialNumber = serialNumber;
        this.technician = technician;
        this.category = category;
        this.company = company;
        this.status = status != null ? status : "Active";
        this.createdAt = createdAt;
    }

    public static EquipmentBuilder builder() {
        return new EquipmentBuilder();
    }

    public static class EquipmentBuilder {
        private Long id;
        private String name;
        private User employee;
        private String department;
        private String serialNumber;
        private User technician;
        private String category;
        private String company;
        private String status = "Active";
        private LocalDateTime createdAt;

        public EquipmentBuilder id(Long id) { this.id = id; return this; }
        public EquipmentBuilder name(String name) { this.name = name; return this; }
        public EquipmentBuilder employee(User employee) { this.employee = employee; return this; }
        public EquipmentBuilder department(String department) { this.department = department; return this; }
        public EquipmentBuilder serialNumber(String serialNumber) { this.serialNumber = serialNumber; return this; }
        public EquipmentBuilder technician(User technician) { this.technician = technician; return this; }
        public EquipmentBuilder category(String category) { this.category = category; return this; }
        public EquipmentBuilder company(String company) { this.company = company; return this; }
        public EquipmentBuilder status(String status) { this.status = status; return this; }
        public EquipmentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Equipment build() {
            return new Equipment(id, name, employee, department, serialNumber, technician, category, company, status, createdAt);
        }
    }

    // Helper getters for JSON compatibility with client
    @JsonProperty("employee_id")
    public Long getEmployeeId() {
        return employee != null ? employee.getId() : null;
    }

    @JsonProperty("employee_name")
    public String getEmployeeName() {
        return employee != null ? employee.getFullName() : null;
    }

    @JsonProperty("technician_id")
    public Long getTechnicianId() {
        return technician != null ? technician.getId() : null;
    }

    @JsonProperty("technician_name")
    public String getTechnicianName() {
        return technician != null ? technician.getFullName() : null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public User getEmployee() { return employee; }
    public void setEmployee(User employee) { this.employee = employee; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public User getTechnician() { return technician; }
    public void setTechnician(User technician) { this.technician = technician; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
