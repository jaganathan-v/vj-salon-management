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
    maps_link TEXT
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
    booking_date DATE NOT NULL,
    booking_time TIME NOT NULL,
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
    comments TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Daily Stylist Service Log & Payment Tracking
CREATE TABLE daily_log (
    id BIGSERIAL PRIMARY KEY,
    stylist_id BIGINT REFERENCES stylists(id) ON DELETE SET NULL,
    service_name VARCHAR(100) NOT NULL,
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

-- 3. SEED INITIAL DATA

-- Shop Settings
INSERT INTO shop_settings (id, shop_name, tagline, address, phone, email, since_year, maps_link)
VALUES (1, 'VIJAYAN SALON', 'Excellence in Every Cut', 'No.2 pillayar kovil street, Ponnammapet, Salem - 636001 (near Mariyamman kovil)', '6374402014', 'vijayansalon@gmail.com', 2010, 'https://maps.google.com/maps?q=Ponnammapet+Salem+636001&output=embed')
ON CONFLICT (id) DO NOTHING;

-- Shop Status (Initial: Open)
INSERT INTO shop_status (id, is_open, note)
VALUES (1, TRUE, 'Welcome to VIJAYAN SALON! Open for walk-ins and bookings.')
ON CONFLICT (id) DO NOTHING;

-- Admin Login: VJADMIN / vj@admin2024 (BCrypt hashed)
INSERT INTO admin_config (id, admin_code, password_hash, email)
VALUES (1, 'VJADMIN', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6a', 'admin@vjsalon.com')
ON CONFLICT (id) DO NOTHING;

-- Stylists: VJS001 / stylist1 and VJS002 / stylist2 (BCrypt hashed)
INSERT INTO stylists (stylist_code, name, password_hash, status, active)
VALUES 
('VJS001', 'Vijayan', '$2a$10$slI9muZK4PwEb4VRP3RGIO.DQUI5VMPHXIVNAFuJSIRmFqQcLiT9W', 'FREE', TRUE),
('VJS002', 'Kumar', '$2a$10$slI9muZK4PwEb4VRP3RGIO.DQUI5VMPHXIVNAFuJSIRmFqQcLiT9W', 'BUSY', TRUE)
ON CONFLICT (stylist_code) DO NOTHING;

-- Multilingual Services
INSERT INTO services (name_en, name_ta, name_hi, price, offer_pct, category, description, display_order)
VALUES 
('Haircut', 'முடி வெட்டு', 'बाल कटाई', 80.00, 0, 'HAIR', 'Professional stylish haircut tailored to face shape.', 1),
('Beard Trim', 'தாடி கத்திரி', 'दाढ़ी ट्रिम', 50.00, 0, 'BEARD', 'Sharp beard shape, grooming and outline trimming.', 2),
('Shave', 'ஷேவ்', 'शेव', 60.00, 0, 'BEARD', 'Smooth hot-towel clean razor shave.', 3),
('Hair Colour', 'முடி நிறம்', 'बाल रंग', 300.00, 10, 'HAIR', 'Natural black and rich dark brown ammonia-free hair dye.', 4),
('Facial', 'முகப்பூச்சு', 'फेशियल', 200.00, 0, 'SKIN', 'Refreshing fruit scrub and skin cleansing massage.', 5),
('Head Massage', 'தலை மசாஜ்', 'सिर मालिश', 100.00, 0, 'HAIR', 'Relaxing 20-minute herbal oil scalp massage.', 6),
('Kids Haircut', 'குழந்தை முடி வெட்டு', 'बच्चों का कटाई', 60.00, 0, 'HAIR', 'Gentle, friendly haircut for children.', 7),
('Threading', 'நூல் நீக்கம்', 'थ्रेडिंग', 30.00, 0, 'SKIN', 'Clean eyebrow and forehead precision threading.', 8);

-- Payment QR
INSERT INTO payment_qr (id, upi_id, display_name, qr_image_path)
VALUES (1, '6374402014@okbizaxis', 'VIJAYAN SALOON', 'assets/uploads/qr/payment_qr.jpg')
ON CONFLICT (id) DO NOTHING;

-- Achievements
INSERT INTO achievements (title_en, title_ta, title_hi, description, year, icon)
VALUES 
('Best Salon in Salem', 'சேலத்தின் சிறந்த சலூன் விருது', 'सलेम का सर्वश्रेष्ठ सैलून', 'Recognized by Salem Business Council for excellence in customer grooming.', 2018, '🏆'),
('500+ Happy Regular Clients', '500+ திருப்திகரமான வாடிக்கையாளர்கள்', '500+ खुश नियमित ग्राहक', 'Milestone of 500 loyal repeating clients across Ponnammapet.', 2020, '⭐'),
('Google 4.8★ Top Rated', 'கூகிள் 4.8★ உயர் மதிப்பீடு', 'गूगल 4.8★ शीर्ष रेटेड', 'Consistently rated 4.8+ stars for hygiene and punctuality.', 2023, '🎖');

-- Active Offers
INSERT INTO offers (title_en, title_ta, title_hi, description, discount_pct, valid_from, valid_until, active)
VALUES 
('Festival Grooming Special', 'திருவிழா சிறப்பு தள்ளுபடி', 'त्योहार विशेष छूट', 'Get 20% discount on all Hair Colour & Facial combos this season!', 20, CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months', TRUE);

-- Inventory
INSERT INTO inventory (item_name, category, current_count, unit, low_threshold, notes)
VALUES 
('Gillette Razor Blades', 'Razor', 18, 'pcs', 5, 'Standard double-edge blades'),
('Clinic Plus Shampoo', 'Shampoo', 4, 'bottles', 5, 'Hair wash station bottles'),
('Old Spice Shaving Cream', 'Cream', 8, 'pcs', 3, 'Hot lather cream'),
('Godrej Expert Hair Colour', 'Colour', 6, 'packs', 3, 'Natural black sachets'),
('Herbal Massage Oil', 'Oil', 5, 'bottles', 2, 'Cooling scalp oil'),
('Cotton Towels', 'Towel', 25, 'pcs', 10, 'Fresh sterilized salon towels');

-- Sample Bookings
INSERT INTO bookings (client_name, contact, service_names, booking_date, booking_time, notes, status)
VALUES 
('Rajesh Kumar', '9876543210', 'Haircut, Beard Trim', CURRENT_DATE, '10:30:00', 'Regular trim, please keep sides short', 'CONFIRMED'),
('Muthu Selvam', '8765432109', 'Hair Colour', CURRENT_DATE, '12:00:00', 'First time hair colour', 'PENDING');

-- Sample Feedback
INSERT INTO feedback (client_name, service_rating, shop_rating, worker_rating, timing_rating, overall_rating, comments)
VALUES 
('Anand G.', 5, 5, 5, 4, 5, 'Wonderful haircut by Vijayan! Very polite and hygienic environment.');

-- Sample Daily Logs
INSERT INTO daily_log (service_name, quantity, amount, payment_type, log_date)
VALUES 
('Haircut', 2, 160.00, 'CASH', CURRENT_DATE),
('Beard Trim', 1, 50.00, 'ONLINE', CURRENT_DATE);

-- 4. CREATE INDEXES FOR FAST QUERYING
CREATE INDEX idx_bookings_date ON bookings(booking_date);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_daily_log_date ON daily_log(log_date);
CREATE INDEX idx_services_category ON services(category);
