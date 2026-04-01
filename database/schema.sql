-- ============================================================
--  BookBridge — Database Schema & Sample Data
--  Run this in MySQL before starting the backend
-- ============================================================

CREATE DATABASE IF NOT EXISTS bookbridge_db;
USE bookbridge_db;

-- Users
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    department  VARCHAR(100),
    phone       VARCHAR(20),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Books
CREATE TABLE IF NOT EXISTS books (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id     BIGINT NOT NULL,
    title        VARCHAR(200) NOT NULL,
    author       VARCHAR(150) NOT NULL,
    subject      VARCHAR(100),
    edition      VARCHAR(50),
    condition    ENUM('NEW','GOOD','FAIR','WORN') DEFAULT 'GOOD',
    description  TEXT,
    status       ENUM('AVAILABLE','REQUESTED','LENT') DEFAULT 'AVAILABLE',
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Book Requests
CREATE TABLE IF NOT EXISTS book_requests (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id      BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status       ENUM('PENDING','ACCEPTED','REJECTED','RETURNED') DEFAULT 'PENDING',
    message      VARCHAR(500),
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id)      REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
--  Sample Data (passwords are BCrypt hashes of "pass1234")
-- ============================================================
INSERT INTO users (name, email, password, department, phone) VALUES
('Arjun Mehta',    'arjun@college.edu',  '$2a$10$dummy', 'Computer Science', '9876543210'),
('Sneha Kulkarni', 'sneha@college.edu',  '$2a$10$dummy', 'Electronics',      '9123456780'),
('Rohan Joshi',    'rohan@college.edu',  '$2a$10$dummy', 'Mechanical',       '9988776655');

INSERT INTO books (owner_id, title, author, subject, edition, condition, description, status) VALUES
(1, 'Operating System Concepts', 'Silberschatz', 'OS',           '10th', 'GOOD', 'Slight highlights on ch3',  'AVAILABLE'),
(1, 'DBMS by Korth',             'Korth',        'Database',     '6th',  'NEW',  'Never used, gifted copy',   'AVAILABLE'),
(2, 'Digital Electronics',       'Floyd',        'Electronics',  '8th',  'FAIR', 'Notes written inside',      'AVAILABLE'),
(2, 'Engineering Maths Vol 1',   'B.S. Grewal',  'Mathematics',  '44th', 'GOOD', 'All chapters intact',       'AVAILABLE'),
(3, 'Design & Analysis of Algo', 'Cormen',       'Algorithms',   '3rd',  'GOOD', 'Clean copy, no marks',      'AVAILABLE');
