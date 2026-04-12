-- Smart Zoo Management System Database Schema

CREATE DATABASE IF NOT EXISTS college_smartzoo;
USE college_smartzoo;

-- Tabel roles
CREATE TABLE IF NOT EXISTS roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255)
);

-- Tabel users
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Insert 5 roles
INSERT INTO roles (role_name, description) VALUES
('Admin', 'Super user dengan akses penuh ke seluruh sistem'),
('Veterinarian', 'Dokter hewan yang menangani kesehatan dan perawatan hewan'),
('Zookeeper', 'Penjaga kebun binatang yang mengurus hewan dan kandang'),
('Ticketing', 'Staff penjualan tiket dan pengelolaan pengunjung'),
('Researcher', 'Peneliti yang melakukan observasi dan riset hewan');

-- Insert 5 seed users (satu per role)
INSERT INTO users (username, password, full_name, role_id) VALUES
('admin', 'admin123', 'Administrator Utama', 1),
('drvet', 'vet123', 'Dr. Anisa Veteriner', 2),
('keeper1', 'keeper123', 'Budi Penjaga', 3),
('tiket1', 'tiket123', 'Citra Kasir', 4),
('peneliti', 'riset123', 'Deni Peneliti', 5);
