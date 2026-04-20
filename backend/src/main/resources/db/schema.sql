-- =============================================
-- DISASTER RELIEF PLATFORM - DATABASE SCHEMA
-- =============================================

CREATE DATABASE IF NOT EXISTS disaster_relief_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE disaster_relief_db;

-- =============================================
-- ROLES
-- =============================================
CREATE TABLE IF NOT EXISTS roles (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        ENUM('ROLE_ADMIN','ROLE_COORDINATOR','ROLE_VOLUNTEER','ROLE_DONOR') NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =============================================
-- USERS
-- =============================================
CREATE TABLE IF NOT EXISTS users (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    email        VARCHAR(100) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    full_name    VARCHAR(100) NOT NULL,
    phone        VARCHAR(20),
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_username (username)
) ENGINE=InnoDB;

-- =============================================
-- USER_ROLES (join table)
-- =============================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =============================================
-- DISASTER TYPES
-- =============================================
CREATE TABLE IF NOT EXISTS disaster_types (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icon        VARCHAR(50),
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =============================================
-- LOCATIONS
-- =============================================
CREATE TABLE IF NOT EXISTS locations (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(200) NOT NULL,
    address      TEXT,
    city         VARCHAR(100),
    state        VARCHAR(100),
    country      VARCHAR(100) DEFAULT 'India',
    pincode      VARCHAR(10),
    latitude     DECIMAL(10,7),
    longitude    DECIMAL(10,7),
    location_type ENUM('DROP_POINT','SHELTER','HOSPITAL','DISTRIBUTION_CENTER','CAMP') NOT NULL,
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_locations_lat_lng (latitude, longitude),
    INDEX idx_locations_type (location_type)
) ENGINE=InnoDB;

-- =============================================
-- DISASTERS
-- =============================================
CREATE TABLE IF NOT EXISTS disasters (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    disaster_type_id BIGINT NOT NULL,
    severity        ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL DEFAULT 'MEDIUM',
    status          ENUM('REPORTED','ACTIVE','CONTAINED','RESOLVED','CLOSED') NOT NULL DEFAULT 'REPORTED',
    latitude        DECIMAL(10,7),
    longitude       DECIMAL(10,7),
    location_name   VARCHAR(200),
    affected_area_km DECIMAL(8,2),
    affected_people  INT DEFAULT 0,
    reported_by     BIGINT NOT NULL,
    start_date      DATETIME,
    end_date        DATETIME,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (disaster_type_id) REFERENCES disaster_types(id),
    FOREIGN KEY (reported_by) REFERENCES users(id),
    INDEX idx_disasters_status (status),
    INDEX idx_disasters_severity (severity),
    INDEX idx_disasters_lat_lng (latitude, longitude)
) ENGINE=InnoDB;

-- =============================================
-- VOLUNTEERS
-- =============================================
CREATE TABLE IF NOT EXISTS volunteers (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE,
    skills          TEXT,
    languages       VARCHAR(255),
    experience_years INT DEFAULT 0,
    availability    ENUM('AVAILABLE','BUSY','UNAVAILABLE','ON_LEAVE') NOT NULL DEFAULT 'AVAILABLE',
    latitude        DECIMAL(10,7),
    longitude       DECIMAL(10,7),
    address         TEXT,
    emergency_contact VARCHAR(20),
    is_verified     BOOLEAN DEFAULT FALSE,
    total_hours     DECIMAL(8,2) DEFAULT 0,
    rating          DECIMAL(3,2) DEFAULT 0,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_volunteers_availability (availability),
    INDEX idx_volunteers_lat_lng (latitude, longitude)
) ENGINE=InnoDB;

-- =============================================
-- VICTIMS
-- =============================================
CREATE TABLE IF NOT EXISTS victims (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name    VARCHAR(100) NOT NULL,
    phone        VARCHAR(20),
    address      TEXT,
    latitude     DECIMAL(10,7),
    longitude    DECIMAL(10,7),
    family_size  INT DEFAULT 1,
    special_needs TEXT,
    status       ENUM('REGISTERED','SHELTERED','RECEIVING_AID','EVACUATED','RECOVERED') NOT NULL DEFAULT 'REGISTERED',
    disaster_id  BIGINT NOT NULL,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (disaster_id) REFERENCES disasters(id),
    INDEX idx_victims_disaster (disaster_id),
    INDEX idx_victims_status (status)
) ENGINE=InnoDB;

-- =============================================
-- RELIEF REQUESTS
-- =============================================
CREATE TABLE IF NOT EXISTS relief_requests (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    victim_id        BIGINT NOT NULL,
    disaster_id      BIGINT NOT NULL,
    request_type     ENUM('FOOD','WATER','MEDICAL','SHELTER','CLOTHING','RESCUE','OTHER') NOT NULL,
    description      TEXT,
    urgency_level    TINYINT NOT NULL DEFAULT 3 CHECK (urgency_level BETWEEN 1 AND 5),
    quantity_needed  INT,
    status           ENUM('PENDING','ASSIGNED','IN_PROGRESS','FULFILLED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    assigned_to      BIGINT,
    fulfilled_at     DATETIME,
    notes            TEXT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (victim_id) REFERENCES victims(id),
    FOREIGN KEY (disaster_id) REFERENCES disasters(id),
    FOREIGN KEY (assigned_to) REFERENCES volunteers(id),
    INDEX idx_relief_status (status),
    INDEX idx_relief_urgency (urgency_level DESC),
    INDEX idx_relief_disaster (disaster_id)
) ENGINE=InnoDB;

-- =============================================
-- PAYMENTS
-- =============================================
CREATE TABLE IF NOT EXISTS payments (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id            BIGINT,
    amount             DECIMAL(12,2) NOT NULL,
    currency           VARCHAR(3) NOT NULL DEFAULT 'INR',
    status             ENUM('CREATED','PENDING','CAPTURED','FAILED','REFUNDED') NOT NULL DEFAULT 'CREATED',
    provider           ENUM('RAZORPAY','STRIPE','MANUAL') NOT NULL DEFAULT 'RAZORPAY',
    payment_order_id   VARCHAR(100) UNIQUE,
    payment_id         VARCHAR(100) UNIQUE,
    signature          VARCHAR(255),
    donor_name         VARCHAR(100),
    donor_email        VARCHAR(100),
    donor_phone        VARCHAR(20),
    failure_reason     TEXT,
    created_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_payments_status (status),
    INDEX idx_payments_order_id (payment_order_id),
    INDEX idx_payments_payment_id (payment_id)
) ENGINE=InnoDB;

-- =============================================
-- PAYMENT EVENTS (webhook idempotency)
-- =============================================
CREATE TABLE IF NOT EXISTS payment_events (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id  BIGINT NOT NULL,
    event_type  VARCHAR(100) NOT NULL,
    payload     JSON,
    processed   BOOLEAN DEFAULT FALSE,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payments(id),
    UNIQUE KEY uk_payment_event (payment_id, event_type)
) ENGINE=InnoDB;

-- =============================================
-- DONATIONS
-- =============================================
CREATE TABLE IF NOT EXISTS donations (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id   BIGINT,
    disaster_id  BIGINT,
    amount       DECIMAL(12,2),
    donation_type ENUM('MONETARY','FOOD','CLOTHING','MEDICINE','EQUIPMENT','OTHER') NOT NULL DEFAULT 'MONETARY',
    description  TEXT,
    status       ENUM('PENDING','CONFIRMED','DISTRIBUTED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    donor_name   VARCHAR(100) NOT NULL,
    donor_email  VARCHAR(100),
    donor_phone  VARCHAR(20),
    is_anonymous BOOLEAN DEFAULT FALSE,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE SET NULL,
    FOREIGN KEY (disaster_id) REFERENCES disasters(id) ON DELETE SET NULL,
    INDEX idx_donations_status (status),
    INDEX idx_donations_disaster (disaster_id)
) ENGINE=InnoDB;

-- =============================================
-- INVENTORY
-- =============================================
CREATE TABLE IF NOT EXISTS inventory (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_name    VARCHAR(200) NOT NULL,
    category     ENUM('FOOD','WATER','MEDICINE','CLOTHING','EQUIPMENT','SHELTER','OTHER') NOT NULL,
    quantity     INT NOT NULL DEFAULT 0,
    unit         VARCHAR(50) NOT NULL DEFAULT 'units',
    location_id  BIGINT,
    disaster_id  BIGINT,
    min_threshold INT DEFAULT 10,
    expiry_date  DATE,
    version      BIGINT DEFAULT 0,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE SET NULL,
    FOREIGN KEY (disaster_id) REFERENCES disasters(id) ON DELETE SET NULL,
    CHECK (quantity >= 0),
    INDEX idx_inventory_category (category),
    INDEX idx_inventory_disaster (disaster_id),
    INDEX idx_inventory_low_stock (quantity, min_threshold)
) ENGINE=InnoDB;

-- =============================================
-- ASSIGNMENTS
-- =============================================
CREATE TABLE IF NOT EXISTS assignments (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    volunteer_id     BIGINT NOT NULL,
    disaster_id      BIGINT NOT NULL,
    relief_request_id BIGINT,
    assigned_by      BIGINT NOT NULL,
    status           ENUM('ASSIGNED','ACCEPTED','DECLINED','IN_PROGRESS','COMPLETED','CANCELLED') NOT NULL DEFAULT 'ASSIGNED',
    notes            TEXT,
    assigned_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    accepted_at      DATETIME,
    completed_at     DATETIME,
    hours_logged     DECIMAL(6,2),
    FOREIGN KEY (volunteer_id) REFERENCES volunteers(id),
    FOREIGN KEY (disaster_id) REFERENCES disasters(id),
    FOREIGN KEY (relief_request_id) REFERENCES relief_requests(id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_by) REFERENCES users(id),
    INDEX idx_assignments_volunteer (volunteer_id),
    INDEX idx_assignments_disaster (disaster_id),
    INDEX idx_assignments_status (status)
) ENGINE=InnoDB;

-- =============================================
-- NOTIFICATIONS
-- =============================================
CREATE TABLE IF NOT EXISTS notifications (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT NOT NULL,
    title             VARCHAR(200) NOT NULL,
    message           TEXT NOT NULL,
    notification_type ENUM('ASSIGNMENT','DISASTER_ALERT','DONATION','REQUEST_UPDATE','SYSTEM','INVENTORY_ALERT') NOT NULL,
    reference_type    VARCHAR(50),
    reference_id      BIGINT,
    is_read           BOOLEAN DEFAULT FALSE,
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notifications_user (user_id),
    INDEX idx_notifications_read (user_id, is_read),
    INDEX idx_notifications_created (created_at DESC)
) ENGINE=InnoDB;

-- =============================================
-- AUDIT LOGS
-- =============================================
CREATE TABLE IF NOT EXISTS audit_logs (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT,
    action       VARCHAR(100) NOT NULL,
    entity_type  VARCHAR(100) NOT NULL,
    entity_id    BIGINT,
    old_value    JSON,
    new_value    JSON,
    ip_address   VARCHAR(45),
    user_agent   VARCHAR(255),
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_entity (entity_type, entity_id),
    INDEX idx_audit_created (created_at DESC)
) ENGINE=InnoDB;
