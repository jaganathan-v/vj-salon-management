-- ==============================================================================
-- VIJAYAN SALON MANAGEMENT SYSTEM — POSTGRESQL DATABASE SCHEMA & SEED DATA
-- Location: Salem, Tamil Nadu, India
-- Database: PostgreSQL 14+ / 16
-- ==============================================================================

-- 1. DROP EXISTING TABLES (Optional for fresh install)
DROP TABLE IF EXISTS daily_log CASCADE;
DROP TABLE IF EXISTS feedback CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS inventory CASCADE;
DROP TABLE IF EXISTS media_assets CASCADE;
DROP TABLE IF EXISTS advertisements CASCADE;
DROP TABLE IF EXISTS offers CASCADE;
DROP TABLE IF EXISTS achievements CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS services CASCADE;
DROP TABLE IF EXISTS stylists CASCADE;
DROP TABLE IF EXISTS admin_config CASCADE;
DROP TABLE IF EXISTS shop_status CASCADE;
DROP TABLE IF EXISTS shop_settings CASCADE;
DROP TABLE IF EXISTS payment_qr CASCADE;

-- 2. CREATE TABLES

-- Shop Settings
CREATE TABLE shop_settings (
    id BIGINT PRIMARY KEY DEFAULT 1,
    shop_name VARCHAR(100) NOT NULL DEFAULT 'VIJAYAN SALON',
    tagline VARCHAR(200) DEFAULT 'Excellence in Every Cut',
    address TEXT NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    since_year INTEGER DEFAULT 2010,
    happy_clients INTEGER NOT NULL DEFAULT 500,
    google_rating NUMERIC(2, 1) NOT NULL DEFAULT 4.8,
    maps_link TEXT,
    site_theme VARCHAR(10) NOT NULL DEFAULT 'dark',
    site_language VARCHAR(10) NOT NULL DEFAULT 'en'
);

-- Shop Live Open / Close Status
CREATE TABLE shop_status (
    id BIGINT PRIMARY KEY DEFAULT 1,
    is_open BOOLEAN NOT NULL DEFAULT FALSE,
    opened_at TIMESTAMP,
    closed_at TIMESTAMP,
    note VARCHAR(255)
);

-- Admin Config & Password
CREATE TABLE admin_config (
    id BIGINT PRIMARY KEY DEFAULT 1,
    admin_code VARCHAR(50) UNIQUE NOT NULL DEFAULT 'VJADMIN',
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100)
);

-- Stylist Staff Accounts
CREATE TABLE stylists (
    id BIGSERIAL PRIMARY KEY,
    stylist_code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'FREE', -- FREE, BUSY, FOOD_BREAK
    skills TEXT,
    available_at TIMESTAMP,
    home_service_status VARCHAR(20) NOT NULL DEFAULT 'FREE', -- FREE, BUSY
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Salon Services (Multilingual: En, Ta, Hi)
CREATE TABLE services (
    id BIGSERIAL PRIMARY KEY,
    name_en VARCHAR(100) NOT NULL,
    name_ta VARCHAR(100),
    name_hi VARCHAR(100),
    price NUMERIC(10,2) NOT NULL,
    offer_pct INTEGER DEFAULT 0,
    category VARCHAR(50) DEFAULT 'HAIR', -- HAIR, BEARD, SKIN, OTHER
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    display_order INTEGER DEFAULT 0
);

-- Client Bookings
CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    client_name VARCHAR(100) NOT NULL,
    contact VARCHAR(20) NOT NULL,
    service_names TEXT,
    total_amount NUMERIC(10, 2),
    booking_date DATE NOT NULL,
    booking_time TIME NOT NULL,
    service_location VARCHAR(20) NOT NULL DEFAULT 'AT_SHOP', -- AT_SHOP, AT_HOME
    address TEXT,
    notes TEXT,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, CONFIRMED, COMPLETED, CANCELLED
    stylist_id BIGINT REFERENCES stylists(id) ON DELETE SET NULL,
    payment_type VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Client 5-Dimension Feedback
CREATE TABLE feedback (
    id BIGSERIAL PRIMARY KEY,
    client_name VARCHAR(100) DEFAULT 'Anonymous',
    service_rating INTEGER CHECK (service_rating BETWEEN 1 AND 5),
    shop_rating INTEGER CHECK (shop_rating BETWEEN 1 AND 5),
    worker_rating INTEGER CHECK (worker_rating BETWEEN 1 AND 5),
    timing_rating INTEGER CHECK (timing_rating BETWEEN 1 AND 5),
    overall_rating INTEGER CHECK (overall_rating BETWEEN 1 AND 5),
    booking_id BIGINT REFERENCES bookings(id) ON DELETE SET NULL,
    comments TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Daily Stylist Service Log & Payment Tracking
CREATE TABLE daily_log (
    id BIGSERIAL PRIMARY KEY,
    stylist_id BIGINT REFERENCES stylists(id) ON DELETE SET NULL,
    service_name VARCHAR(100) NOT NULL,
    service_location VARCHAR(20) DEFAULT 'AT_SHOP',
    quantity INTEGER DEFAULT 1,
    payment_type VARCHAR(20) DEFAULT 'CASH', -- CASH, ONLINE
    amount NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    log_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Shop Inventory & Supplies
CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    current_count INTEGER DEFAULT 0,
    unit VARCHAR(20) DEFAULT 'pcs',
    low_threshold INTEGER DEFAULT 5,
    notes TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Advertisements & Photos
CREATE TABLE advertisements (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(10) DEFAULT 'IMAGE', -- IMAGE, VIDEO
    file_path VARCHAR(500),
    title VARCHAR(200),
    display_order INTEGER DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Uploaded media is stored in Postgres so it survives ephemeral app filesystems
CREATE TABLE media_assets (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(120) NOT NULL,
    content BYTEA NOT NULL
);

-- Daily Events & Special Notices
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    event_date DATE,
    title_en VARCHAR(200) NOT NULL,
    title_ta VARCHAR(200),
    title_hi VARCHAR(200),
    description TEXT,
    is_recurring BOOLEAN DEFAULT FALSE,
    category VARCHAR(50)
);

-- Shop Achievements & Awards
CREATE TABLE achievements (
    id BIGSERIAL PRIMARY KEY,
    title_en VARCHAR(200) NOT NULL,
    title_ta VARCHAR(200),
    title_hi VARCHAR(200),
    description TEXT,
    year INTEGER,
    icon VARCHAR(20) DEFAULT '🏆'
);

-- Promotional Offers & Discounts
CREATE TABLE offers (
    id BIGSERIAL PRIMARY KEY,
    title_en VARCHAR(200) NOT NULL,
    title_ta VARCHAR(200),
    title_hi VARCHAR(200),
    description TEXT,
    discount_pct INTEGER DEFAULT 0,
    valid_from DATE,
    valid_until DATE,
    active BOOLEAN DEFAULT TRUE
);

-- Payment QR & UPI Info
CREATE TABLE payment_qr (
    id BIGINT PRIMARY KEY DEFAULT 1,
    upi_id VARCHAR(100) DEFAULT '6374402014@okbizaxis',
    display_name VARCHAR(100) DEFAULT 'VIJAYAN SALOON',
    qr_image_path VARCHAR(500) DEFAULT 'assets/uploads/qr/payment_qr.jpg',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. SEED INITIAL DATA (only required system defaults)

-- Shop Status (Initial: Open)
INSERT INTO shop_status (id, is_open, note)
VALUES (1, TRUE, '')
ON CONFLICT (id) DO NOTHING;

-- Admin Login: VJADMIN / vj@admin2024 (BCrypt hashed)
INSERT INTO admin_config (id, admin_code, password_hash, email)
VALUES (1, 'VJADMIN', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6a', 'admin@vjsalon.com')
ON CONFLICT (id) DO NOTHING;
-- 4. CREATE INDEXES FOR FAST QUERYING
CREATE INDEX idx_bookings_date ON bookings(booking_date);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_daily_log_date ON daily_log(log_date);
CREATE INDEX idx_services_category ON services(category);
