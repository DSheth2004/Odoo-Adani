package com.maintsync.service;

import com.maintsync.model.Team;
import com.maintsync.model.User;
import com.maintsync.repository.TeamRepository;
import com.maintsync.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TechnicianPlaceholderService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public TechnicianPlaceholderService(UserRepository userRepository, TeamRepository teamRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * Creates a placeholder technician for the given team to avoid a creation deadlock.
     * The placeholder will have a generated email address and a flag indicating it is a placeholder.
     * The placeholder is added to the team's members collection.
     */
    public void createPlaceholderTechnician(Team team) {
        if (team == null) return;
        User placeholder = User.builder()
                .fullName("Unassigned Technician")
                .email("placeholder+" + team.getId() + "@maintsync.local")
                .role("technician")
                .authProvider("SYSTEM")
                .providerId(UUID.randomUUID().toString())

                .build();
        // Persist placeholder user
        placeholder = userRepository.save(placeholder);
        // Associate with team
        team.getMembers().add(placeholder);
        teamRepository.save(team);
    }
}
