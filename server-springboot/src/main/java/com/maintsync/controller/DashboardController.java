package com.maintsync.controller;

import com.maintsync.dto.AppDtos.DashboardResponse;
import com.maintsync.dto.AppDtos.DashboardStats;
import com.maintsync.model.User;
import com.maintsync.repository.UserRepository;
import com.maintsync.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    public DashboardController(DashboardService dashboardService, UserRepository userRepository) {
        this.dashboardService = dashboardService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<DashboardResponse> getDashboard(Authentication auth) {
        Long userId = getUserId(auth);
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        if (user == null) {
            DashboardStats stats = dashboardService.computeGlobalStats();
            return ResponseEntity.ok(DashboardResponse.builder().stats(stats).build());
        }

        return ResponseEntity.ok(dashboardService.getDashboardForUser(user));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<DashboardStats> getStats() {
        return ResponseEntity.ok(dashboardService.computeGlobalStats());
    }

    private Long getUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) return null;
        try {
            return Long.parseLong(auth.getPrincipal().toString());
        } catch (Exception e) {
            return null;
        }
    }
}
