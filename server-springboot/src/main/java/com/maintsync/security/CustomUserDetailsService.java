package com.maintsync.security;

import com.maintsync.model.User;
import com.maintsync.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = null;
        if (identifier.contains("@")) {
            user = userRepository.findByEmail(identifier.trim().toLowerCase()).orElse(null);
        }
        if (user == null) {
            try {
                Long id = Long.parseLong(identifier.trim());
                user = userRepository.findById(id).orElse(null);
            } catch (NumberFormatException ignored) {}
        }
        if (user == null) {
            user = userRepository.findByUsername(identifier.trim()).orElse(null);
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found with identifier: " + identifier);
        }

        String role = user.getRole() != null ? user.getRole().toUpperCase() : "EMPLOYEE";
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getId()),
                user.getPasswordHash() != null ? user.getPasswordHash() : "",
                authorities
        );
    }
}
