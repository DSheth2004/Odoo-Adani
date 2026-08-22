package com.maintsync.controller;

import com.maintsync.dto.AuthDtos.*;
import com.maintsync.model.User;
import com.maintsync.repository.UserRepository;
import com.maintsync.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        String cleanName = request.getFullName().trim();
        String cleanEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(cleanEmail)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email already registered"));
        }

        User user = User.builder()
                .fullName(cleanName)
                .email(cleanEmail)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("employee")
                .authProvider("LOCAL")
                .build();

        user = userRepository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole(), user.getFullName());

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFullName(user.getFullName());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        userDto.setDepartment(user.getDepartment());
        userDto.setCompany(user.getCompany());

        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmail(cleanEmail);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }

        User user = userOpt.get();
        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole(), user.getFullName());

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFullName(user.getFullName());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        userDto.setDepartment(user.getDepartment());
        userDto.setCompany(user.getCompany());

        return ResponseEntity.ok(new AuthResponse(token, userDto));
    }
}
