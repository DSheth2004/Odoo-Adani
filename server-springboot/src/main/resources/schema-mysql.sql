-- MaintSync MySQL Schema
CREATE DATABASE IF NOT EXISTS maintenance_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE maintenance_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    username VARCHAR(50) UNIQUE,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'employee',
    department VARCHAR(80),
    company VARCHAR(120),
    auth_provider VARCHAR(30) NOT NULL DEFAULT 'LOCAL',
    placeholder TINYINT(1) NOT NULL DEFAULT 0,
    provider_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Teams Table
CREATE TABLE IF NOT EXISTS teams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    company VARCHAR(120),
    member_user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_teams_member FOREIGN KEY (member_user_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 3. Team Members Join Table
CREATE TABLE IF NOT EXISTS team_members (
    team_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (team_id, user_id),
    CONSTRAINT fk_tm_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    CONSTRAINT fk_tm_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Equipment Table
CREATE TABLE IF NOT EXISTS equipment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    employee_id BIGINT NULL,
    department VARCHAR(80),
    serial_number VARCHAR(60) NOT NULL UNIQUE,
    technician_id BIGINT NULL,
    category VARCHAR(80),
    company VARCHAR(120),
    status VARCHAR(20) NOT NULL DEFAULT 'Active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_eq_employee FOREIGN KEY (employee_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_eq_technician FOREIGN KEY (technician_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 5. Maintenance Requests Table
CREATE TABLE IF NOT EXISTS maintenance_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipment_id BIGINT NULL,
    requested_by_id BIGINT NULL,
    assigned_technician_id BIGINT NULL,
    maintenance_type VARCHAR(50) NOT NULL,
    priority INT NOT NULL DEFAULT 3,
    status VARCHAR(30) NOT NULL DEFAULT 'New Request',
    maintenance_for VARCHAR(30) NOT NULL DEFAULT 'equipment',
    work_center VARCHAR(120),
    team_name VARCHAR(120),
    notes TEXT,
    instructions TEXT,
    scheduled_start DATETIME NULL,
    scheduled_end DATETIME NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_mr_equipment FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE RESTRICT,
    CONSTRAINT fk_mr_requested_by FOREIGN KEY (requested_by_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_mr_assigned_tech FOREIGN KEY (assigned_technician_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Indexes for optimal lookup performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_equipment_name ON equipment(name);
CREATE INDEX idx_equipment_serial ON equipment(serial_number);
CREATE INDEX idx_requests_status ON maintenance_requests(status);
CREATE INDEX idx_requests_scheduled_start ON maintenance_requests(scheduled_start);
