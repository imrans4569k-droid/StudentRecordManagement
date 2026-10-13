-- ==============================================================
-- Student Record Management System - Database Setup Script
-- Compatible with MySQL 5.5, 5.7, 8.0, and newer
-- ==============================================================

-- 1. Create Database
CREATE DATABASE IF NOT EXISTS student_db;
USE student_db;

-- 2. Create Students Table
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    roll VARCHAR(50) NOT NULL UNIQUE,
    department VARCHAR(100) NOT NULL,
    year VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL
);

-- 3. Insert Initial Sample Records
INSERT INTO students (name, roll, department, year, email, phone)
VALUES
('Mohammed Imran', 'CSE-2024-001', 'Computer Science', '3rd Year', 'imran@example.com', '9876543210'),
('Ayesha Fatima', 'ECE-2024-042', 'Electronics & Communication', '2nd Year', 'ayesha@example.com', '9876543211'),
('Rahul Sharma', 'MECH-2024-015', 'Mechanical Engineering', '4th Year', 'rahul@example.com', '9876543212')
ON DUPLICATE KEY UPDATE name=VALUES(name);
