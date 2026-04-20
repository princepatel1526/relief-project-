USE disaster_relief_db;

-- =============================================
-- SEED ROLES
-- =============================================
INSERT IGNORE INTO roles (name, description) VALUES
('ROLE_CITIZEN', 'Citizen reporting incidents and requesting support'),
('ROLE_VOLUNTEER', 'Field volunteer providing relief'),
('ROLE_RESPONDER', 'Professional first responder'),
('ROLE_NGO_COORDINATOR', 'NGO coordinator managing partner operations'),
('ROLE_ADMIN', 'System administrator with operational access'),
('ROLE_SUPER_ADMIN', 'Super administrator with full cross-region access');

-- =============================================
-- SEED USERS (password = "password123" BCrypt encoded)
-- =============================================
INSERT IGNORE INTO users (username, email, password, full_name, phone, is_active) VALUES
('admin',       'admin@disasterrelief.org',      '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'System Admin',        '9000000001', TRUE),
('superadmin',  'superadmin@disasterrelief.org', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Global Super Admin',  '9000000009', TRUE),
('ngo_coord1',  'ngo.coord@disasterrelief.org',  '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Rahul NGO Coordinator','9000000002', TRUE),
('volunteer1',  'volunteer1@disasterrelief.org', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Priya Sharma',        '9000000003', TRUE),
('volunteer2',  'volunteer2@disasterrelief.org', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Amit Kumar',          '9000000004', TRUE),
('responder1',  'responder1@disasterrelief.org', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Riya Responder',      '9000000008', TRUE),
('citizen1',    'citizen1@example.com',          '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Sneha Citizen',       '9000000005', TRUE);

-- Assign roles
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_ADMIN' WHERE u.username = 'admin';
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_SUPER_ADMIN' WHERE u.username = 'superadmin';
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_NGO_COORDINATOR' WHERE u.username = 'ngo_coord1';
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_VOLUNTEER' WHERE u.username IN ('volunteer1', 'volunteer2');
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_RESPONDER' WHERE u.username = 'responder1';
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_CITIZEN' WHERE u.username = 'citizen1';

-- =============================================
-- SEED DISASTER TYPES
-- =============================================
INSERT IGNORE INTO disaster_types (name, description, icon) VALUES
('Flood',          'Flooding due to heavy rainfall or river overflow',   'flood'),
('Earthquake',     'Seismic activity causing structural damage',         'earthquake'),
('Cyclone',        'Tropical cyclone with strong winds and rainfall',    'cyclone'),
('Drought',        'Extended period of water scarcity',                  'drought'),
('Landslide',      'Sudden movement of rock and debris down a slope',    'landslide'),
('Industrial Fire','Fire at industrial or commercial sites',             'fire'),
('Tsunami',        'Large ocean waves caused by seismic activity',       'tsunami'),
('Chemical Spill', 'Hazardous chemical leak requiring evacuation',       'chemical');

-- =============================================
-- SEED LOCATIONS
-- =============================================
INSERT IGNORE INTO locations (name, address, city, state, latitude, longitude, location_type, is_active) VALUES
('Mumbai Relief Camp A',     'Dharavi, Mumbai',             'Mumbai',    'Maharashtra', 19.0396, 72.8527, 'CAMP',               TRUE),
('Mumbai Distribution Hub',  'BKC, Mumbai',                 'Mumbai',    'Maharashtra', 19.0622, 72.8697, 'DISTRIBUTION_CENTER', TRUE),
('Pune Drop Point 1',        'Shivajinagar, Pune',          'Pune',      'Maharashtra', 18.5314, 73.8446, 'DROP_POINT',         TRUE),
('Chennai Shelter B',        'Tambaram, Chennai',           'Chennai',   'Tamil Nadu',  12.9249, 80.1000, 'SHELTER',            TRUE),
('Kerala Medical Camp',      'Ernakulam, Kochi',            'Kochi',     'Kerala',      9.9312,  76.2673, 'HOSPITAL',           TRUE);

-- =============================================
-- SEED VOLUNTEERS
-- =============================================
INSERT IGNORE INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, is_verified) VALUES
(3, 'First Aid, Search and Rescue, CPR', 'Hindi, English, Marathi', 3, 'AVAILABLE', 19.0760, 72.8777, 'Andheri West, Mumbai', TRUE),
(4, 'Medical, Food Distribution, Logistics', 'Hindi, English', 2, 'AVAILABLE', 18.5204, 73.8567, 'Koregaon Park, Pune', TRUE);

-- =============================================
-- SEED DISASTERS
-- =============================================
INSERT IGNORE INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_people, reported_by, start_date) VALUES
('Mumbai Coastal Flooding 2024',
 'Severe flooding in coastal areas of Mumbai due to record monsoon rainfall. Multiple localities submerged.',
 1, 'CRITICAL', 'ACTIVE', 19.0760, 72.8777, 'Mumbai, Maharashtra', 5000, 1, '2024-07-15 08:00:00'),
('Pune Landslide Event',
 'Landslide near Pune hills affecting residential areas. Multiple families displaced.',
 5, 'HIGH', 'ACTIVE', 18.5204, 73.8567, 'Pune, Maharashtra', 200, 2, '2024-08-01 14:30:00');

-- =============================================
-- SEED VICTIMS
-- =============================================
INSERT IGNORE INTO victims (full_name, phone, address, latitude, longitude, family_size, special_needs, status, disaster_id) VALUES
('Ramesh Pawar',    '9111111111', 'Dharavi, Mumbai',     19.0396, 72.8527, 5, 'Elderly parents',     'REGISTERED', 1),
('Sunita Jadhav',   '9111111112', 'Kurla, Mumbai',       19.0728, 72.8826, 3, 'Infant child',        'SHELTERED',  1),
('Manoj Gupta',     '9111111113', 'Baner, Pune',         18.5590, 73.7868, 4, NULL,                  'REGISTERED', 2);

-- =============================================
-- SEED RELIEF REQUESTS
-- =============================================
INSERT IGNORE INTO relief_requests (victim_id, disaster_id, request_type, description, urgency_level, quantity_needed, status) VALUES
(1, 1, 'FOOD',    'Family needs cooked food for 5 people',    5, 5,  'PENDING'),
(1, 1, 'WATER',   'Clean drinking water urgently needed',     5, 20, 'PENDING'),
(2, 1, 'MEDICAL', 'Infant requires pediatric checkup',        4, 1,  'PENDING'),
(3, 2, 'SHELTER', 'House collapsed, family needs shelter',    5, 1,  'ASSIGNED'),
(3, 2, 'FOOD',    'Three days without food, 4 family members',4, 4,  'PENDING');

-- =============================================
-- SEED INVENTORY
-- =============================================
INSERT IGNORE INTO inventory (item_name, category, quantity, unit, location_id, disaster_id, min_threshold) VALUES
('Rice (5kg bags)',         'FOOD',     500,  'bags',    1, 1, 50),
('Mineral Water (1L)',      'WATER',    2000, 'bottles', 1, 1, 200),
('Ready-to-eat Meals',     'FOOD',     300,  'packets', 2, 1, 30),
('First Aid Kit',          'MEDICINE', 100,  'kits',    5, 1, 10),
('Blankets',               'CLOTHING', 400,  'pieces',  1, 1, 40),
('Tarpaulin Sheets',       'SHELTER',  150,  'sheets',  1, 1, 20),
('ORS Packets',            'MEDICINE', 500,  'packets', 5, 1, 50),
('Bread Loaves',           'FOOD',     200,  'loaves',  3, 2, 20),
('Drinking Water (5L)',    'WATER',    300,  'cans',    3, 2, 30),
('Sleeping Bags',          'SHELTER',  80,   'pieces',  4, 2, 10);

-- =============================================
-- SEED NEWS FEED
-- =============================================
INSERT INTO news_updates (title, summary, content, image_url, disaster_type, severity, status, location, latitude, longitude, source_incident_id, affected_people, rescue_progress, created_by)
SELECT
  'Mumbai Coastal Flooding: Evacuation Corridors Open',
  'Response teams opened high-ground evacuation corridors and expanded shelter capacity in Mumbai coastal zones.',
  'Emergency teams have deployed boats and medical units across high-impact wards. Supply routes remain open and water levels are being monitored hourly. Citizens are advised to follow official alerts and avoid submerged roads.',
  'https://images.unsplash.com/photo-1547683905-f686c993aae5?auto=format&fit=crop&w=1400&q=80',
  'Flood',
  'CRITICAL',
  'ACTIVE',
  'Mumbai, Maharashtra',
  19.0760,
  72.8777,
  1,
  5000,
  58,
  1
WHERE NOT EXISTS (SELECT 1 FROM news_updates WHERE title = 'Mumbai Coastal Flooding: Evacuation Corridors Open');

INSERT INTO news_timeline_updates (news_id, update_text, update_timestamp)
SELECT n.id, 'Initial emergency bulletin released; shelters activated.', DATE_SUB(NOW(), INTERVAL 6 HOUR)
FROM news_updates n
WHERE n.title = 'Mumbai Coastal Flooding: Evacuation Corridors Open'
  AND NOT EXISTS (SELECT 1 FROM news_timeline_updates t WHERE t.news_id = n.id);
