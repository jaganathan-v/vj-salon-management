-- ==============================================================================
-- VIJAYAN SALON MANAGEMENT SYSTEM — SEED DATA (POSTGRESQL)
-- ==============================================================================

INSERT INTO shop_settings (id, shop_name, tagline, address, phone, email, since_year, happy_clients, google_rating, maps_link, site_theme, site_language)
VALUES (1, 'VIJAYAN SALON', 'Excellence in Every Cut', 'No.2 pillayar kovil street, Ponnammapet, Salem - 636001', '6374402014', 'admin@vjsalon.com', 2010, 500, 4.8, 'https://maps.google.com', 'dark', 'en')
ON CONFLICT (id) DO NOTHING;

INSERT INTO shop_status (id, is_open, note)
VALUES (1, TRUE, 'Welcome to VIJAYAN SALON')
ON CONFLICT (id) DO NOTHING;

INSERT INTO admin_config (id, admin_code, password_hash, email)
VALUES (1, 'VJADMIN', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6a', 'admin@vjsalon.com')
ON CONFLICT (id) DO NOTHING;

INSERT INTO admin_config (id, admin_code, password_hash, email)
VALUES (2, 'admin', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StBQ/59GbCyNo', 'admin@vjsalon.com')
ON CONFLICT (id) DO NOTHING;

INSERT INTO payment_qr (id, upi_id, display_name, qr_image_path)
VALUES (1, '6374402014@okbizaxis', 'VIJAYAN SALOON', 'assets/uploads/qr/payment_qr.jpg')
ON CONFLICT (id) DO NOTHING;

INSERT INTO services (id, name_en, name_ta, name_hi, price, offer_pct, category, description, active, display_order)
VALUES (1, 'Classic Haircut', 'முடி வெட்டுதல்', 'हेयरकट', 150.00, 0, 'HAIR', 'Professional scissor cut and style', TRUE, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO services (id, name_en, name_ta, name_hi, price, offer_pct, category, description, active, display_order)
VALUES (2, 'Beard Trim & Shape', 'தாடி திருத்துதல்', 'दाढ़ी ट्रिम', 100.00, 0, 'BEARD', 'Precision beard shaping and conditioning', TRUE, 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO stylists (id, stylist_code, name, password_hash, status, home_service_status, active)
VALUES (1, 'STY01', 'Vijayan V', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StBQ/59GbCyNo', 'FREE', 'FREE', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO offers (id, title_en, title_ta, title_hi, description, discount_pct, valid_from, valid_until, active)
VALUES (1, 'Festival Special', 'பண்டிகை சலுகை', 'त्योहार विशेष', 'Get 20% off all grooming services', 20, CURRENT_DATE, CURRENT_DATE + INTERVAL '90 days', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO achievements (id, title_en, title_ta, title_hi, description, year, icon)
VALUES (1, 'Best Salon Salem', 'சிறந்த நிலையம் சேலம்', 'सर्वश्रेष्ठ सैलून', 'Voted best men salon in Salem', 2022, 'trophy')
ON CONFLICT (id) DO NOTHING;
