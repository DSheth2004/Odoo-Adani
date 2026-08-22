package com.maintsync.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 120)
    @JsonProperty("full_name")
    private String fullName;

    @Column(name = "username", length = 50, unique = true)
    private String username;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "role", nullable = false, length = 20)
    private String role = "employee";
    @Column(name = "placeholder", nullable = false)
    private boolean placeholder = false;

    @Column(name = "department", length = 80)
    private String department;

    @Column(name = "company", length = 120)
    private String company;

    @Column(name = "auth_provider", nullable = false, length = 30)
    @JsonProperty("auth_provider")
    private String authProvider = "LOCAL";

    @Column(name = "provider_id", length = 100)
    @JsonProperty("provider_id")
    private String providerId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public User() {}

    public User(Long id, String fullName, String username, String email, String passwordHash, String role, String department, String company, String authProvider, String providerId, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role != null ? role : "employee";
        this.department = department;
        this.company = company;
        this.authProvider = authProvider != null ? authProvider : "LOCAL";
        this.providerId = providerId;
        this.createdAt = createdAt;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long id;
        private String fullName;
        private String username;
        private String email;
        private String passwordHash;
        private String role = "employee";
        private String department;
        private String company;
        private String authProvider = "LOCAL";
        private String providerId;
        private LocalDateTime createdAt;
        private boolean placeholder = false;

        public UserBuilder id(Long id) { this.id = id; return this; }
        public UserBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public UserBuilder username(String username) { this.username = username; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public UserBuilder role(String role) { this.role = role; return this; }
        public UserBuilder department(String department) { this.department = department; return this; }
        public UserBuilder company(String company) { this.company = company; return this; }
        public UserBuilder authProvider(String authProvider) { this.authProvider = authProvider; return this; }
        public UserBuilder providerId(String providerId) { this.providerId = providerId; return this; }
        public UserBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public UserBuilder placeholder(boolean placeholder) { this.placeholder = placeholder; return this; }
        public User build() {
            User user = new User(id, fullName, username, email, passwordHash, role, department, company, authProvider, providerId, createdAt);
            user.setPlaceholder(this.placeholder);
            return user;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getAuthProvider() { return authProvider; }
    public void setAuthProvider(String authProvider) { this.authProvider = authProvider; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isPlaceholder() { return placeholder; }
    public void setPlaceholder(boolean placeholder) { this.placeholder = placeholder; }
}
