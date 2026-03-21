-- ============================================================
--   GoBuddy Database Schema
--   Run this script before starting the Spring Boot application
-- ============================================================

CREATE DATABASE IF NOT EXISTS gobuddy_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gobuddy_db;

-- ─── USERS ────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id      BIGINT        NOT NULL AUTO_INCREMENT,
    name         VARCHAR(100)  NOT NULL,
    email        VARCHAR(100)  NOT NULL UNIQUE,
    password     VARCHAR(255)  NOT NULL,
    phone_number VARCHAR(15),
    role         VARCHAR(20)   NOT NULL DEFAULT 'user',
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    INDEX idx_users_email (email)
) ENGINE=InnoDB;

-- ─── SEARCHES ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS searches (
    search_id     BIGINT       NOT NULL AUTO_INCREMENT,
    user_id       BIGINT,
    from_location VARCHAR(100) NOT NULL,
    to_location   VARCHAR(100) NOT NULL,
    travel_date   DATE         NOT NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (search_id),
    CONSTRAINT fk_search_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE SET NULL,
    INDEX idx_searches_user (user_id)
) ENGINE=InnoDB;

-- ─── TRANSPORT OPTIONS ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS transport_options (
    option_id      BIGINT         NOT NULL AUTO_INCREMENT,
    search_id      BIGINT,
    type           ENUM('BUS','TRAIN','FLIGHT') NOT NULL,
    provider_name  VARCHAR(100)   NOT NULL,
    price          DECIMAL(10,2)  NOT NULL,
    duration       VARCHAR(50),
    departure_time VARCHAR(10),
    arrival_time   VARCHAR(10),
    redirect_url   VARCHAR(255)   NOT NULL,
    PRIMARY KEY (option_id),
    CONSTRAINT fk_option_search
        FOREIGN KEY (search_id) REFERENCES searches(search_id)
        ON DELETE CASCADE,
    INDEX idx_options_search (search_id)
) ENGINE=InnoDB;

-- ─── FEEDBACKS ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS feedbacks (
    feedback_id BIGINT   NOT NULL AUTO_INCREMENT,
    user_id     BIGINT   NOT NULL,
    message     TEXT     NOT NULL,
    rating      INT      NOT NULL CHECK (rating BETWEEN 1 AND 5),
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (feedback_id),
    CONSTRAINT fk_feedback_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ─── SEED: Default Admin User ─────────────────────────────────
-- Password: admin123  (BCrypt hash — change in production!)
INSERT IGNORE INTO users (name, email, password, role)
VALUES (
    'GoBuddy Admin',
    'admin@gobuddy.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'admin'
);
