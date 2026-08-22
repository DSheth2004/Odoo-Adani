package com.maintsync.controller;

import com.maintsync.dto.AppDtos.RoleUpdateRequest;
import com.maintsync.model.User;
import com.maintsync.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "q", required = false) String q
    ) {
        String searchRole = (role != null && !role.isBlank()) ? role.trim() : null;
        String searchQuery = (q != null && !q.isBlank()) ? q.trim() : null;
        List<User> users = userRepository.searchUsers(searchRole, searchQuery);
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        if (request.getRole() == null || request.getRole().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role"));
        }

        String nextRole = request.getRole().trim().toLowerCase();
        if (!List.of("admin", "technician", "employee").contains(nextRole)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Role must be admin, technician, or employee"));
        }

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        user.setRole(nextRole);
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }
}
