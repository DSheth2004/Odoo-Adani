package com.maintsync.controller;

import com.maintsync.dto.AppDtos.EquipmentRequest;
import com.maintsync.model.Equipment;
import com.maintsync.model.User;
import com.maintsync.repository.EquipmentRepository;
import com.maintsync.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    public EquipmentController(EquipmentRepository equipmentRepository, UserRepository userRepository) {
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getEquipment(
            @RequestParam(value = "q", required = false) String q,
            Authentication auth
    ) {
        String role = getRole(auth);
        Long userId = getUserId(auth);

        Long employeeIdFilter = "employee".equalsIgnoreCase(role) ? userId : null;
        String searchQuery = (q != null && !q.isBlank()) ? q.trim() : null;

        List<Equipment> equipmentList = equipmentRepository.searchEquipment(employeeIdFilter, searchQuery);
        return ResponseEntity.ok(equipmentList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEquipmentById(@PathVariable Long id) {
        return equipmentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEquipment(@RequestBody EquipmentRequest req) {
        if (req.getName() == null || req.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name is required"));
        }
        if (req.getSerialNumber() == null || req.getSerialNumber().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Serial number is required"));
        }
        if (req.getTechnicianId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Technician is required"));
        }

        if (equipmentRepository.existsBySerialNumber(req.getSerialNumber().trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Serial number already exists"));
        }

        User technician = userRepository.findById(req.getTechnicianId()).orElse(null);
        if (technician == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Technician not found"));
        }

        User employee = null;
        if (req.getEmployeeId() != null) {
            employee = userRepository.findById(req.getEmployeeId()).orElse(null);
        }

        Equipment equipment = Equipment.builder()
                .name(req.getName().trim())
                .serialNumber(req.getSerialNumber().trim())
                .technician(technician)
                .employee(employee)
                .department(req.getDepartment() != null ? req.getDepartment().trim() : null)
                .category(req.getCategory() != null ? req.getCategory().trim() : null)
                .company(req.getCompany() != null ? req.getCompany().trim() : null)
                .status(req.getStatus() != null ? req.getStatus().trim() : "Active")
                .build();

        equipment = equipmentRepository.save(equipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(equipment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEquipment(@PathVariable Long id, @RequestBody EquipmentRequest req) {
        Optional<Equipment> eqOpt = equipmentRepository.findById(id);
        if (eqOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Equipment equipment = eqOpt.get();

        if (req.getName() != null && !req.getName().isBlank()) equipment.setName(req.getName().trim());
        if (req.getSerialNumber() != null && !req.getSerialNumber().isBlank()) equipment.setSerialNumber(req.getSerialNumber().trim());
        if (req.getDepartment() != null) equipment.setDepartment(req.getDepartment().trim());
        if (req.getCategory() != null) equipment.setCategory(req.getCategory().trim());
        if (req.getCompany() != null) equipment.setCompany(req.getCompany().trim());
        if (req.getStatus() != null) equipment.setStatus(req.getStatus().trim());

        if (req.getTechnicianId() != null) {
            User technician = userRepository.findById(req.getTechnicianId()).orElse(null);
            equipment.setTechnician(technician);
        }

        if (req.getEmployeeId() != null) {
            User employee = userRepository.findById(req.getEmployeeId()).orElse(null);
            equipment.setEmployee(employee);
        }

        equipment = equipmentRepository.save(equipment);
        return ResponseEntity.ok(equipment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEquipment(@PathVariable Long id) {
        if (!equipmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        equipmentRepository.deleteById(id);
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
