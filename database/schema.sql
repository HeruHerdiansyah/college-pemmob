-- ============================================================
-- Smart Zoo Management System — Database Schema
-- Database: smartzoo
-- ============================================================

CREATE DATABASE IF NOT EXISTS smartzoo;
USE smartzoo;

-- ============================================================
-- Tabel: roles
-- ============================================================
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

-- Seed 5 roles
INSERT IGNORE INTO roles (role_name) VALUES
('Admin'),
('Veterinarian'),
('Zookeeper'),
('Ticketing'),
('Researcher');

-- ============================================================
-- Tabel: users
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Seed 5 users (1 per role)
INSERT IGNORE INTO users (full_name, username, password, role_id) VALUES
('Administrator', 'admin', 'admin123', 1),
('Dr. Rina Veteriner', 'vet_rina', 'vet123', 2),
('Budi Keeper', 'keeper_budi', 'keeper123', 3),
('Siti Tiket', 'tiket_siti', 'tiket123', 4),
('Prof. Andi Riset', 'riset_andi', 'riset123', 5);
