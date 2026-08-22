package com.maintsync.controller;

import com.maintsync.dto.AppDtos.AddTeamMemberRequest;
import com.maintsync.dto.AppDtos.TeamCreateRequest;
import com.maintsync.model.Team;
import com.maintsync.model.User;
import com.maintsync.repository.TeamRepository;
import com.maintsync.repository.UserRepository;

import org.springframework.http.HttpStatus;
import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TeamController {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    public TeamController(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/teams")
    public ResponseEntity<List<Team>> getTeams(Authentication auth) {
        String role = getRole(auth);
        Long userId = getUserId(auth);

        if ("technician".equalsIgnoreCase(role) && userId != null) {
            return ResponseEntity.ok(teamRepository.findTeamsByMemberUserId(userId));
        }
        return ResponseEntity.ok(teamRepository.findAllOrderedByName());
    }

    @GetMapping("/team-options")
    public ResponseEntity<?> getTeamOptions(Authentication auth) {
        String role = getRole(auth);
        Long userId = getUserId(auth);

        List<Team> teams;
        if ("technician".equalsIgnoreCase(role) && userId != null) {
            teams = teamRepository.findTeamsByMemberUserId(userId);
        } else {
            teams = teamRepository.findAllOrderedByName();
        }

        List<Map<String, Object>> options = teams.stream()
                .map(t -> Map.<String, Object>of("id", t.getId(), "name", t.getName()))
                .toList();

        return ResponseEntity.ok(options);
    }

    @PostMapping("/teams")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTeam(@RequestBody TeamCreateRequest req) {
        if (req.getName() == null || req.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "name is required"));
        }
        if (req.getMemberUserId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "member_user_id is required"));
        }

        // Idempotency: if a team with the same name already exists, return it instead of creating a new one
        Optional<Team> existing = teamRepository.findByName(req.getName().trim());
        if (existing.isPresent()) {
            Team existingTeam = existing.get();
            Map<String, Object> payload = new HashMap<>();
            payload.put("team", existingTeam);
            payload.put("users", userRepository.findAll());
            return ResponseEntity.ok(payload);
        }

        // The existence check is kept for safety, though the previous block handles most cases
        if (teamRepository.existsByName(req.getName().trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Team name already exists"));
        }

        Optional<User> memberOpt = userRepository.findById(req.getMemberUserId());
        if (memberOpt.isEmpty() || !"technician".equalsIgnoreCase(memberOpt.get().getRole())) {
            return ResponseEntity.badRequest().body(Map.of("error", "member_user_id must be a technician"));
        }

        Team team = Team.builder()
                .name(req.getName().trim())
                .company(req.getCompany() != null ? req.getCompany().trim() : null)
                .memberUser(memberOpt.get())
                .build();
        team.getMembers().add(memberOpt.get());

        team = teamRepository.save(team);
        // Reload team (no placeholder technician)
        Team refreshedTeam = teamRepository.findById(team.getId()).orElse(team);
        Map<String, Object> payload = new HashMap<>();
        payload.put("team", refreshedTeam);
        payload.put("users", userRepository.findAll());
        return ResponseEntity.status(HttpStatus.CREATED).body(payload);
    }

    @PostMapping("/teams/{id}/members")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addTeamMember(@PathVariable Long id, @RequestBody AddTeamMemberRequest req) {
        if (req.getUserId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "user_id is required"));
        }

        Optional<Team> teamOpt = teamRepository.findById(id);
        if (teamOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Team not found"));
        }

        Optional<User> userOpt = userRepository.findById(req.getUserId());
        if (userOpt.isEmpty() || !"technician".equalsIgnoreCase(userOpt.get().getRole())) {
            return ResponseEntity.badRequest().body(Map.of("error", "user_id must be a technician"));
        }

        Team team = teamOpt.get();
        User user = userOpt.get();
        // Idempotency: if the user is already a member, do nothing and return OK
        boolean alreadyMember = team.getMembers().stream().anyMatch(u -> u.getId().equals(user.getId()));
        if (alreadyMember) {
            return ResponseEntity.ok(Map.of("ok", true, "message", "User already a member"));
        }
        team.getMembers().add(user);
        teamRepository.save(team);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("ok", true));
    }

    @DeleteMapping("/teams/{id}/members/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeTeamMember(@PathVariable Long id, @PathVariable Long userId) {
        Optional<Team> teamOpt = teamRepository.findById(id);
        if (teamOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Team not found"));
        }

        Team team = teamOpt.get();
        boolean removed = team.getMembers().removeIf(u -> u.getId().equals(userId));
        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Member not found in team"));
        }

        teamRepository.save(team);
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
