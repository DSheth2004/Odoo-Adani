package com.maintsync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public static class SignupRequest {
        @NotBlank(message = "Full name is required")
        @JsonProperty("full_name")
        private String fullName;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String token;
        private UserDto user;

        public AuthResponse() {}
        public AuthResponse(String token, UserDto user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public UserDto getUser() { return user; }
        public void setUser(UserDto user) { this.user = user; }
    }

    public static class UserDto {
        private Long id;
        @JsonProperty("full_name")
        private String fullName;
        private String email;
        private String role;
        private String department;
        private String company;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
    }
}
