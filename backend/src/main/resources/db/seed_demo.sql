-- =============================================================
-- DISASTER RELIEF PLATFORM — MASTER DEMO SEED
-- Run once against disaster_relief_db.
-- All passwords: password123
-- BCrypt hash ($2a$12$): $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i
-- Admin fallback password: Admin@2024 (created by AdminUserInitializer)
-- =============================================================

USE disaster_relief_db;
START TRANSACTION;
SET @now = NOW();
SET @pw  = '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i';

-- =============================================================
-- 1. ROLES (idempotent)
-- =============================================================
INSERT IGNORE INTO roles (name, description) VALUES
  ('ROLE_CITIZEN',         'Citizen reporting incidents and requesting support'),
  ('ROLE_VOLUNTEER',       'Field volunteer providing relief and coordination'),
  ('ROLE_RESPONDER',       'Professional first responder — search & rescue, emergency medical'),
  ('ROLE_NGO_COORDINATOR', 'NGO coordinator managing partner operations and resources'),
  ('ROLE_ADMIN',           'System administrator with full operational access'),
  ('ROLE_SUPER_ADMIN',     'Super administrator with cross-region and system-level access');

-- =============================================================
-- 2. DISASTER TYPES (idempotent)
-- =============================================================
INSERT IGNORE INTO disaster_types (name, description, icon) VALUES
  ('Flood',          'Flooding due to heavy rainfall or river overflow',      'flood'),
  ('Cyclone',        'Tropical cyclone with strong winds and rainfall',        'cyclone'),
  ('Earthquake',     'Seismic activity causing structural damage',             'earthquake'),
  ('Landslide',      'Sudden movement of rock and debris down a slope',       'landslide'),
  ('Industrial Fire','Fire at industrial or commercial sites',                 'fire'),
  ('Urban Fire',     'Large urban fire event',                                 'fire'),
  ('Tsunami',        'Large ocean waves caused by seismic activity',           'tsunami'),
  ('Chemical Spill', 'Hazardous chemical leak requiring evacuation',           'chemical'),
  ('Drought',        'Extended period of water scarcity and crop failure',     'drought'),
  ('Heatwave',       'Prolonged extreme heat conditions causing health crisis','heatwave');

-- =============================================================
-- 3. USERS  (all passwords = password123)
-- =============================================================

-- ── SUPER ADMIN ───────────────────────────────────────────────
INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'superadmin', 'superadmin@disasterrelief.org', @pw, 'Platform Super Admin', '9000000000', TRUE, DATE_SUB(@now, INTERVAL 90 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'superadmin');

-- ── ADMINS ────────────────────────────────────────────────────
INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'admin', 'admin@disasterrelief.org', @pw, 'System Administrator', '9000000001', TRUE, DATE_SUB(@now, INTERVAL 85 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'admin2', 'admin2@disasterrelief.org', @pw, 'Rajesh Mehta', '9000000002', TRUE, DATE_SUB(@now, INTERVAL 80 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin2');

-- ── NGO COORDINATORS ─────────────────────────────────────────
INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'ops_lead_mumbai', 'ops.mumbai@disasterrelief.org', @pw, 'Ananya Iyer', '9000000011', TRUE, DATE_SUB(@now, INTERVAL 75 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'ops_lead_mumbai');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'ops_lead_chennai', 'ops.chennai@disasterrelief.org', @pw, 'Vignesh Raman', '9000000012', TRUE, DATE_SUB(@now, INTERVAL 73 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'ops_lead_chennai');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'ops_lead_delhi', 'ops.delhi@disasterrelief.org', @pw, 'Prateek Khanna', '9000000013', TRUE, DATE_SUB(@now, INTERVAL 70 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'ops_lead_delhi');

-- ── RESPONDERS ────────────────────────────────────────────────
INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'responder_01', 'resp01@ndrf.gov.in', @pw, 'Cpt. Arun Tiwari', '9000000061', TRUE, DATE_SUB(@now, INTERVAL 65 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'responder_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'responder_02', 'resp02@ndrf.gov.in', @pw, 'Cpt. Shalini Dubey', '9000000062', TRUE, DATE_SUB(@now, INTERVAL 63 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'responder_02');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'responder_03', 'resp03@ndrf.gov.in', @pw, 'Lt. Ramesh Pillai', '9000000063', TRUE, DATE_SUB(@now, INTERVAL 60 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'responder_03');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'responder_04', 'resp04@sdrf.gov.in', @pw, 'Dr. Kavitha Nair', '9000000064', TRUE, DATE_SUB(@now, INTERVAL 58 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'responder_04');

-- ── VOLUNTEERS ────────────────────────────────────────────────
INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'volunteer_mum_01', 'vol.mum01@disasterrelief.org', @pw, 'Priya Sharma', '9000000021', TRUE, DATE_SUB(@now, INTERVAL 68 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'volunteer_mum_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'volunteer_mum_02', 'vol.mum02@disasterrelief.org', @pw, 'Arjun More', '9000000022', TRUE, DATE_SUB(@now, INTERVAL 66 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'volunteer_mum_02');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'volunteer_che_01', 'vol.che01@disasterrelief.org', @pw, 'Meena Subramanian', '9000000023', TRUE, DATE_SUB(@now, INTERVAL 65 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'volunteer_che_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'volunteer_asm_01', 'vol.asm01@disasterrelief.org', @pw, 'Rituporna Das', '9000000024', TRUE, DATE_SUB(@now, INTERVAL 63 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'volunteer_asm_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'volunteer_del_01', 'vol.del01@disasterrelief.org', @pw, 'Suresh Chauhan', '9000000025', TRUE, DATE_SUB(@now, INTERVAL 61 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'volunteer_del_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'volunteer_kol_01', 'vol.kol01@disasterrelief.org', @pw, 'Biplab Chatterjee', '9000000026', TRUE, DATE_SUB(@now, INTERVAL 59 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'volunteer_kol_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'volunteer_pun_01', 'vol.pun01@disasterrelief.org', @pw, 'Gurpreet Singh', '9000000027', TRUE, DATE_SUB(@now, INTERVAL 57 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'volunteer_pun_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'volunteer_hyd_01', 'vol.hyd01@disasterrelief.org', @pw, 'Lakshmi Reddy', '9000000028', TRUE, DATE_SUB(@now, INTERVAL 55 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'volunteer_hyd_01');

-- ── CITIZENS ─────────────────────────────────────────────────
INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'citizen_mumbai_01',    'citizen.mum01@example.com',    @pw, 'Sajid Khan',         '9000000031', TRUE, DATE_SUB(@now, INTERVAL 52 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'citizen_mumbai_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'citizen_chennai_01',   'citizen.che01@example.com',    @pw, 'Lakshmi Narayanan',  '9000000032', TRUE, DATE_SUB(@now, INTERVAL 50 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'citizen_chennai_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'citizen_kolkata_01',   'citizen.kol01@example.com',    @pw, 'Debanjan Roy',       '9000000033', TRUE, DATE_SUB(@now, INTERVAL 48 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'citizen_kolkata_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'citizen_delhi_01',     'citizen.del01@example.com',    @pw, 'Neha Agarwal',       '9000000034', TRUE, DATE_SUB(@now, INTERVAL 46 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'citizen_delhi_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'citizen_delhi_02',     'citizen.del02@example.com',    @pw, 'Mohammad Farouk',    '9000000035', TRUE, DATE_SUB(@now, INTERVAL 44 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'citizen_delhi_02');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'citizen_pune_01',      'citizen.pun01@example.com',    @pw, 'Smita Deshpande',    '9000000036', TRUE, DATE_SUB(@now, INTERVAL 42 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'citizen_pune_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'citizen_pune_02',      'citizen.pun02@example.com',    @pw, 'Rahul Joshi',        '9000000037', TRUE, DATE_SUB(@now, INTERVAL 40 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'citizen_pune_02');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'citizen_hyderabad_01', 'citizen.hyd01@example.com',    @pw, 'Venkat Rao',         '9000000038', TRUE, DATE_SUB(@now, INTERVAL 38 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'citizen_hyderabad_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'citizen_bengaluru_01', 'citizen.blr01@example.com',    @pw, 'Radhika Krishnan',   '9000000039', TRUE, DATE_SUB(@now, INTERVAL 36 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'citizen_bengaluru_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'citizen_guwahati_01',  'citizen.guw01@example.com',    @pw, 'Manas Bhuyan',       '9000000040', TRUE, DATE_SUB(@now, INTERVAL 34 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'citizen_guwahati_01');

-- ── DONORS (mapped to CITIZEN role) ──────────────────────────
INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'donor_corp_01',  'donor.corp01@example.com',  @pw, 'Suryanet Logistics Pvt Ltd',  '9000000041', TRUE, DATE_SUB(@now, INTERVAL 45 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'donor_corp_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'donor_ind_01',   'donor.ind01@example.com',   @pw, 'Neha Kulkarni',               '9000000042', TRUE, DATE_SUB(@now, INTERVAL 43 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'donor_ind_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'donor_corp_02',  'donor.corp02@example.com',  @pw, 'Bharat Tech Solutions',       '9000000043', TRUE, DATE_SUB(@now, INTERVAL 30 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'donor_corp_02');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'donor_corp_03',  'donor.corp03@example.com',  @pw, 'Greenpath Foundation',        '9000000044', TRUE, DATE_SUB(@now, INTERVAL 22 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'donor_corp_03');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'donor_ind_02',   'donor.ind02@example.com',   @pw, 'Arjun Singhvi',               '9000000045', TRUE, DATE_SUB(@now, INTERVAL 14 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'donor_ind_02');

-- =============================================================
-- 4. USER → ROLE MAPPINGS
-- =============================================================
INSERT IGNORE INTO user_roles (user_id, role_id)
  SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_SUPER_ADMIN'
  WHERE u.username = 'superadmin';

INSERT IGNORE INTO user_roles (user_id, role_id)
  SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_ADMIN'
  WHERE u.username IN ('admin', 'admin2');

INSERT IGNORE INTO user_roles (user_id, role_id)
  SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_NGO_COORDINATOR'
  WHERE u.username IN ('ops_lead_mumbai', 'ops_lead_chennai', 'ops_lead_delhi');

INSERT IGNORE INTO user_roles (user_id, role_id)
  SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_RESPONDER'
  WHERE u.username IN ('responder_01', 'responder_02', 'responder_03', 'responder_04');

INSERT IGNORE INTO user_roles (user_id, role_id)
  SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_VOLUNTEER'
  WHERE u.username IN (
    'volunteer_mum_01', 'volunteer_mum_02', 'volunteer_che_01', 'volunteer_asm_01',
    'volunteer_del_01', 'volunteer_kol_01', 'volunteer_pun_01', 'volunteer_hyd_01'
  );

INSERT IGNORE INTO user_roles (user_id, role_id)
  SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_CITIZEN'
  WHERE u.username IN (
    'citizen_mumbai_01', 'citizen_chennai_01', 'citizen_kolkata_01',
    'citizen_delhi_01',  'citizen_delhi_02',   'citizen_pune_01',
    'citizen_pune_02',   'citizen_hyderabad_01','citizen_bengaluru_01',
    'citizen_guwahati_01',
    'donor_corp_01', 'donor_ind_01', 'donor_corp_02', 'donor_corp_03', 'donor_ind_02'
  );

-- =============================================================
-- 5. LOCATIONS / SHELTERS
-- =============================================================
INSERT INTO locations (name, address, city, state, latitude, longitude, location_type, is_active, created_at)
SELECT 'Mumbai Relief Camp - Sion',        'Sion East Grounds',              'Mumbai',        'Maharashtra',   19.0448,  72.8649,  'CAMP',                TRUE, DATE_SUB(@now, INTERVAL 40 DAY)
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Mumbai Relief Camp - Sion');

INSERT INTO locations (name, address, city, state, latitude, longitude, location_type, is_active, created_at)
SELECT 'Mumbai Shelter - Dharavi School',  'Dharavi Municipal School',       'Mumbai',        'Maharashtra',   19.0403,  72.8573,  'SHELTER',             TRUE, DATE_SUB(@now, INTERVAL 39 DAY)
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Mumbai Shelter - Dharavi School');

INSERT INTO locations (name, address, city, state, latitude, longitude, location_type, is_active, created_at)
SELECT 'Navi Mumbai Distribution Hub',     'Vashi Sector 17',                'Navi Mumbai',   'Maharashtra',   19.0771,  73.0008,  'DISTRIBUTION_CENTER', TRUE, DATE_SUB(@now, INTERVAL 38 DAY)
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Navi Mumbai Distribution Hub');

INSERT INTO locations (name, address, city, state, latitude, longitude, location_type, is_active, created_at)
SELECT 'Chennai Shelter - Velachery',      'Velachery Govt School',          'Chennai',       'Tamil Nadu',    12.9752,  80.2209,  'SHELTER',             TRUE, DATE_SUB(@now, INTERVAL 36 DAY)
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Chennai Shelter - Velachery');

INSERT INTO locations (name, address, city, state, latitude, longitude, location_type, is_active, created_at)
SELECT 'Chennai Medical Camp - Perungudi', 'OMR Perungudi',                  'Chennai',       'Tamil Nadu',    12.9601,  80.2446,  'HOSPITAL',            TRUE, DATE_SUB(@now, INTERVAL 35 DAY)
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Chennai Medical Camp - Perungudi');

INSERT INTO locations (name, address, city, state, latitude, longitude, location_type, is_active, created_at)
SELECT 'Kolkata Relief Camp - Howrah',     'Howrah Maidan',                  'Howrah',        'West Bengal',   22.5958,  88.2636,  'CAMP',                TRUE, DATE_SUB(@now, INTERVAL 33 DAY)
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Kolkata Relief Camp - Howrah');

INSERT INTO locations (name, address, city, state, latitude, longitude, location_type, is_active, created_at)
SELECT 'Guwahati Shelter - Beltola',       'Beltola High School',            'Guwahati',      'Assam',         26.1161,  91.7972,  'SHELTER',             TRUE, DATE_SUB(@now, INTERVAL 31 DAY)
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Guwahati Shelter - Beltola');

INSERT INTO locations (name, address, city, state, latitude, longitude, location_type, is_active, created_at)
SELECT 'Delhi Relief Hub - Sarai Kale Khan', 'Sarai Kale Khan Bus Terminal', 'New Delhi',     'Delhi',         28.6053,  77.2574,  'DISTRIBUTION_CENTER', TRUE, DATE_SUB(@now, INTERVAL 28 DAY)
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Delhi Relief Hub - Sarai Kale Khan');

-- =============================================================
-- 6. DISASTERS / INCIDENTS
-- =============================================================
INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Mumbai Monsoon Flooding - Kurla Cluster',
  'Continuous rain caused waterlogging and home displacement in Kurla and Sion pockets. Multiple families stranded in low-lying buildings.',
  dt.id, 'HIGH', 'ACTIVE', 19.0735, 72.8790, 'Kurla, Mumbai', 18.5, 3200,
  u.id, DATE_SUB(@now, INTERVAL 24 DAY), NULL, DATE_SUB(@now, INTERVAL 24 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Flood' AND u.username = 'ops_lead_mumbai'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Mumbai Monsoon Flooding - Kurla Cluster');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Mumbai Building Fire - Bhiwandi Warehouse',
  'Nighttime warehouse fire affected nearby worker housing. Triggered full emergency response. Contained after 4-day operation.',
  dt.id, 'CRITICAL', 'CONTAINED', 19.2813, 73.0483, 'Bhiwandi, Thane', 4.1, 470,
  u.id, DATE_SUB(@now, INTERVAL 20 DAY), DATE_SUB(@now, INTERVAL 16 DAY), DATE_SUB(@now, INTERVAL 20 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Urban Fire' AND u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Mumbai Building Fire - Bhiwandi Warehouse');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Navi Mumbai Heatwave Impact',
  'Heat index crossed dangerous levels for 7 consecutive days. Dehydration and heat-stroke medical demand surged in Nerul and Airoli.',
  dt.id, 'MEDIUM', 'RESOLVED', 19.0330, 73.0297, 'Nerul, Navi Mumbai', 12.0, 850,
  u.id, DATE_SUB(@now, INTERVAL 55 DAY), DATE_SUB(@now, INTERVAL 48 DAY), DATE_SUB(@now, INTERVAL 55 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Heatwave' AND u.username = 'ops_lead_mumbai'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Navi Mumbai Heatwave Impact');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Chennai Cyclone Surge - Marina Belt',
  'Cyclonic winds and coastal surge flooding severely affected settlements and transport corridors along the Marina foreshore.',
  dt.id, 'CRITICAL', 'ACTIVE', 13.0499, 80.2824, 'Marina - Foreshore Estate, Chennai', 27.2, 5100,
  u.id, DATE_SUB(@now, INTERVAL 18 DAY), NULL, DATE_SUB(@now, INTERVAL 18 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Cyclone' AND u.username = 'ops_lead_chennai'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Chennai Cyclone Surge - Marina Belt');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Chennai Flooding - Velachery Basin',
  'Urban flooding due to drainage overflow and intense overnight rainfall submerging Velachery basin residential blocks.',
  dt.id, 'HIGH', 'ACTIVE', 12.9815, 80.2180, 'Velachery, Chennai', 11.6, 1900,
  u.id, DATE_SUB(@now, INTERVAL 12 DAY), NULL, DATE_SUB(@now, INTERVAL 12 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Flood' AND u.username = 'ops_lead_chennai'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Chennai Flooding - Velachery Basin');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Cuddalore Landslide Trigger',
  'Slope failure after heavy rain blocked roads and isolated 4 hamlets. Rescue operation completed in 4 days.',
  dt.id, 'MEDIUM', 'CONTAINED', 11.7447, 79.7680, 'Cuddalore District', 7.8, 320,
  u.id, DATE_SUB(@now, INTERVAL 30 DAY), DATE_SUB(@now, INTERVAL 26 DAY), DATE_SUB(@now, INTERVAL 30 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Landslide' AND u.username = 'ops_lead_chennai'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Cuddalore Landslide Trigger');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Kolkata Riverbank Flood Alert',
  'Hooghly river water level rise affected low-lying wards and temporary settlements along the north riverbank.',
  dt.id, 'HIGH', 'ACTIVE', 22.5726, 88.3639, 'Kolkata North Riverbank', 9.4, 1300,
  u.id, DATE_SUB(@now, INTERVAL 7 DAY), NULL, DATE_SUB(@now, INTERVAL 7 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Flood' AND u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Kolkata Riverbank Flood Alert');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Guwahati Flash Flood - Beltola',
  'Localized flash flooding disrupted schools, public distribution routes and displaced 1,150 residents in Beltola and adjoining wards.',
  dt.id, 'HIGH', 'ACTIVE', 26.1445, 91.7362, 'Beltola, Guwahati', 8.9, 1150,
  u.id, DATE_SUB(@now, INTERVAL 10 DAY), NULL, DATE_SUB(@now, INTERVAL 10 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Flood' AND u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Guwahati Flash Flood - Beltola');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Dibrugarh Embankment Breach Response',
  'Embankment breach impacted multiple villages requiring rescue boats, dry rations, and medical teams.',
  dt.id, 'CRITICAL', 'ACTIVE', 27.4728, 94.9120, 'Dibrugarh, Assam', 21.0, 2400,
  u.id, DATE_SUB(@now, INTERVAL 5 DAY), NULL, DATE_SUB(@now, INTERVAL 5 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Flood' AND u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Dibrugarh Embankment Breach Response');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Bhubaneswar Earth Tremor Preparedness',
  'Minor tremors triggered precautionary evacuations and emergency inspections of civic structures.',
  dt.id, 'LOW', 'CLOSED', 20.2961, 85.8245, 'Bhubaneswar City', 3.2, 140,
  u.id, DATE_SUB(@now, INTERVAL 75 DAY), DATE_SUB(@now, INTERVAL 72 DAY), DATE_SUB(@now, INTERVAL 75 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Earthquake' AND u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Bhubaneswar Earth Tremor Preparedness');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Delhi Industrial Chemical Spill - Okhla',
  'Tanker overturned on NH road releasing hazardous fumes; 600 residents evacuated. Containment team deployed within 2 hours.',
  dt.id, 'HIGH', 'CONTAINED', 28.5510, 77.2698, 'Okhla, New Delhi', 3.5, 620,
  u.id, DATE_SUB(@now, INTERVAL 15 DAY), DATE_SUB(@now, INTERVAL 12 DAY), DATE_SUB(@now, INTERVAL 15 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Chemical Spill' AND u.username = 'ops_lead_delhi'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Delhi Industrial Chemical Spill - Okhla');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Rajasthan Drought Relief - Barmer Region',
  'Prolonged drought has left 8 villages without accessible water sources. Emergency tanker deployment and distribution underway.',
  dt.id, 'MEDIUM', 'ACTIVE', 25.7456, 71.3934, 'Barmer, Rajasthan', 180.0, 4200,
  u.id, DATE_SUB(@now, INTERVAL 35 DAY), NULL, DATE_SUB(@now, INTERVAL 35 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Drought' AND u.username = 'ops_lead_delhi'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Rajasthan Drought Relief - Barmer Region');

-- =============================================================
-- 7. VOLUNTEER PROFILES
-- =============================================================
INSERT INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, emergency_contact, is_verified, total_hours, rating, created_at)
SELECT u.id, 'First Aid, Evacuation Support, Camp Operations', 'Hindi, English, Marathi', 4, 'AVAILABLE', 19.0712, 72.8826, 'Chembur, Mumbai', '9876501201', TRUE, 128, 4.7, DATE_SUB(@now, INTERVAL 60 DAY)
FROM users u WHERE u.username = 'volunteer_mum_01'
  AND NOT EXISTS (SELECT 1 FROM volunteers v WHERE v.user_id = u.id);

INSERT INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, emergency_contact, is_verified, total_hours, rating, created_at)
SELECT u.id, 'Logistics, Food Distribution, Crowd Control', 'Hindi, English', 3, 'BUSY', 19.2183, 72.9781, 'Thane West', '9876501202', TRUE, 96, 4.5, DATE_SUB(@now, INTERVAL 58 DAY)
FROM users u WHERE u.username = 'volunteer_mum_02'
  AND NOT EXISTS (SELECT 1 FROM volunteers v WHERE v.user_id = u.id);

INSERT INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, emergency_contact, is_verified, total_hours, rating, created_at)
SELECT u.id, 'Nursing Support, Shelter Triage, Child Care', 'Tamil, English', 5, 'AVAILABLE', 12.9887, 80.2350, 'Perungudi, Chennai', '9876501203', TRUE, 152, 4.8, DATE_SUB(@now, INTERVAL 57 DAY)
FROM users u WHERE u.username = 'volunteer_che_01'
  AND NOT EXISTS (SELECT 1 FROM volunteers v WHERE v.user_id = u.id);

INSERT INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, emergency_contact, is_verified, total_hours, rating, created_at)
SELECT u.id, 'Boat Rescue, Water Safety, Local Coordination', 'Assamese, Hindi, English', 6, 'AVAILABLE', 26.1615, 91.7671, 'Dispur, Guwahati', '9876501204', TRUE, 181, 4.9, DATE_SUB(@now, INTERVAL 56 DAY)
FROM users u WHERE u.username = 'volunteer_asm_01'
  AND NOT EXISTS (SELECT 1 FROM volunteers v WHERE v.user_id = u.id);

INSERT INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, emergency_contact, is_verified, total_hours, rating, created_at)
SELECT u.id, 'Community Outreach, Food Distribution, Camp Management', 'Hindi, Punjabi, English', 2, 'AVAILABLE', 28.6139, 77.2090, 'Karol Bagh, New Delhi', '9876501205', FALSE, 44, 4.2, DATE_SUB(@now, INTERVAL 54 DAY)
FROM users u WHERE u.username = 'volunteer_del_01'
  AND NOT EXISTS (SELECT 1 FROM volunteers v WHERE v.user_id = u.id);

INSERT INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, emergency_contact, is_verified, total_hours, rating, created_at)
SELECT u.id, 'Water Rescue, Crowd Management, First Aid', 'Bengali, Hindi, English', 4, 'AVAILABLE', 22.5726, 88.3639, 'Howrah, West Bengal', '9876501206', TRUE, 110, 4.6, DATE_SUB(@now, INTERVAL 52 DAY)
FROM users u WHERE u.username = 'volunteer_kol_01'
  AND NOT EXISTS (SELECT 1 FROM volunteers v WHERE v.user_id = u.id);

INSERT INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, emergency_contact, is_verified, total_hours, rating, created_at)
SELECT u.id, 'Medical Support, Emergency Coordination, Triage', 'Punjabi, Hindi, English', 3, 'BUSY', 30.7333, 76.7794, 'Chandigarh', '9876501207', TRUE, 88, 4.4, DATE_SUB(@now, INTERVAL 50 DAY)
FROM users u WHERE u.username = 'volunteer_pun_01'
  AND NOT EXISTS (SELECT 1 FROM volunteers v WHERE v.user_id = u.id);

INSERT INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, emergency_contact, is_verified, total_hours, rating, created_at)
SELECT u.id, 'Logistics, Vehicle Operation, Supply Chain Management', 'Telugu, Hindi, English', 2, 'AVAILABLE', 17.3850, 78.4867, 'Hyderabad, Telangana', '9876501208', FALSE, 36, 4.1, DATE_SUB(@now, INTERVAL 48 DAY)
FROM users u WHERE u.username = 'volunteer_hyd_01'
  AND NOT EXISTS (SELECT 1 FROM volunteers v WHERE v.user_id = u.id);

-- Responder volunteer profiles (required for assignment system)
INSERT INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, emergency_contact, is_verified, total_hours, rating, created_at)
SELECT u.id, 'Search and Rescue, Emergency Medical Response, Rope Techniques', 'Hindi, English', 8, 'AVAILABLE', 19.0760, 72.8777, 'NDRF 3rd Bn, Pune', '9811001001', TRUE, 620, 4.9, DATE_SUB(@now, INTERVAL 65 DAY)
FROM users u WHERE u.username = 'responder_01'
  AND NOT EXISTS (SELECT 1 FROM volunteers v WHERE v.user_id = u.id);

INSERT INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, emergency_contact, is_verified, total_hours, rating, created_at)
SELECT u.id, 'Fire Response, Hazmat Handling, Chemical Decontamination', 'Hindi, English', 7, 'BUSY', 13.0827, 80.2707, 'SDRF TN Unit, Chennai', '9811001002', TRUE, 540, 4.8, DATE_SUB(@now, INTERVAL 63 DAY)
FROM users u WHERE u.username = 'responder_02'
  AND NOT EXISTS (SELECT 1 FROM volunteers v WHERE v.user_id = u.id);

INSERT INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, emergency_contact, is_verified, total_hours, rating, created_at)
SELECT u.id, 'Trauma Care, Triage, Emergency Surgery Support', 'Malayalam, English, Hindi', 10, 'AVAILABLE', 9.9312, 76.2673, 'NDRF Medical Wing, Kochi', '9811001003', TRUE, 780, 5.0, DATE_SUB(@now, INTERVAL 60 DAY)
FROM users u WHERE u.username = 'responder_03'
  AND NOT EXISTS (SELECT 1 FROM volunteers v WHERE v.user_id = u.id);

INSERT INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, emergency_contact, is_verified, total_hours, rating, created_at)
SELECT u.id, 'Urban Search and Rescue, Structural Assessment, Heavy Machinery', 'Hindi, English', 6, 'ON_LEAVE', 26.1445, 91.7362, 'SDRF Assam, Guwahati', '9811001004', TRUE, 420, 4.7, DATE_SUB(@now, INTERVAL 58 DAY)
FROM users u WHERE u.username = 'responder_04'
  AND NOT EXISTS (SELECT 1 FROM volunteers v WHERE v.user_id = u.id);

-- =============================================================
-- 8. VICTIMS
-- =============================================================
INSERT INTO victims (full_name, phone, address, latitude, longitude, family_size, special_needs, status, disaster_id, created_at)
SELECT 'Shabana Sheikh',      '9111110001', 'Nehru Nagar, Kurla',            19.0657, 72.8784, 6, 'Diabetic elder, needs medication',         'RECEIVING_AID', d.id, DATE_SUB(@now, INTERVAL 23 DAY)
FROM disasters d WHERE d.title = 'Mumbai Monsoon Flooding - Kurla Cluster'
  AND NOT EXISTS (SELECT 1 FROM victims v WHERE v.full_name = 'Shabana Sheikh' AND v.disaster_id = d.id);

INSERT INTO victims (full_name, phone, address, latitude, longitude, family_size, special_needs, status, disaster_id, created_at)
SELECT 'Karthik Elangovan',   '9111110002', 'Foreshore Estate Block C',      13.0407, 80.2852, 4, 'Child with chronic asthma',                'SHELTERED',     d.id, DATE_SUB(@now, INTERVAL 17 DAY)
FROM disasters d WHERE d.title = 'Chennai Cyclone Surge - Marina Belt'
  AND NOT EXISTS (SELECT 1 FROM victims v WHERE v.full_name = 'Karthik Elangovan' AND v.disaster_id = d.id);

INSERT INTO victims (full_name, phone, address, latitude, longitude, family_size, special_needs, status, disaster_id, created_at)
SELECT 'Rahima Begum',        '9111110003', 'Beltola Tiniali',               26.1260, 91.8031, 5, 'Pregnant, 7 months',                       'REGISTERED',    d.id, DATE_SUB(@now, INTERVAL 9 DAY)
FROM disasters d WHERE d.title = 'Guwahati Flash Flood - Beltola'
  AND NOT EXISTS (SELECT 1 FROM victims v WHERE v.full_name = 'Rahima Begum' AND v.disaster_id = d.id);

INSERT INTO victims (full_name, phone, address, latitude, longitude, family_size, special_needs, status, disaster_id, created_at)
SELECT 'Prasenjit Bora',      '9111110004', 'Lahowal Village, Dibrugarh',    27.4702, 94.9013, 7, 'Wheelchair-bound; requires flat terrain',  'REGISTERED',    d.id, DATE_SUB(@now, INTERVAL 4 DAY)
FROM disasters d WHERE d.title = 'Dibrugarh Embankment Breach Response'
  AND NOT EXISTS (SELECT 1 FROM victims v WHERE v.full_name = 'Prasenjit Bora' AND v.disaster_id = d.id);

INSERT INTO victims (full_name, phone, address, latitude, longitude, family_size, special_needs, status, disaster_id, created_at)
SELECT 'Indira Moorthy',      '9111110005', 'Velachery South Extension',     12.9728, 80.2141, 3, 'Insulin-dependent elderly woman',          'RECEIVING_AID', d.id, DATE_SUB(@now, INTERVAL 11 DAY)
FROM disasters d WHERE d.title = 'Chennai Flooding - Velachery Basin'
  AND NOT EXISTS (SELECT 1 FROM victims v WHERE v.full_name = 'Indira Moorthy' AND v.disaster_id = d.id);

INSERT INTO victims (full_name, phone, address, latitude, longitude, family_size, special_needs, status, disaster_id, created_at)
SELECT 'Javed Ansari',        '9111110006', 'Kolkata Ward 8, Near Ganges',   22.5910, 88.3521, 8, 'Two children under 5, no permanent address','REGISTERED',    d.id, DATE_SUB(@now, INTERVAL 6 DAY)
FROM disasters d WHERE d.title = 'Kolkata Riverbank Flood Alert'
  AND NOT EXISTS (SELECT 1 FROM victims v WHERE v.full_name = 'Javed Ansari' AND v.disaster_id = d.id);

-- =============================================================
-- 9. RELIEF REQUESTS
-- =============================================================
INSERT INTO relief_requests (victim_id, disaster_id, request_type, description, urgency_level, quantity_needed, status, priority_score, affected_people, has_elderly_children, is_medical_emergency, notes, created_at)
SELECT v.id, d.id, 'WATER', 'Need safe drinking water for 6 family members. Well contaminated.', 5, 18, 'IN_PROGRESS', 78, 6, TRUE, FALSE, 'Escalated: contamination risk confirmed.', DATE_SUB(@now, INTERVAL 22 DAY)
FROM victims v JOIN disasters d ON d.id = v.disaster_id
WHERE v.full_name = 'Shabana Sheikh' AND d.title = 'Mumbai Monsoon Flooding - Kurla Cluster'
  AND NOT EXISTS (SELECT 1 FROM relief_requests r WHERE r.victim_id = v.id AND r.request_type = 'WATER');

INSERT INTO relief_requests (victim_id, disaster_id, request_type, description, urgency_level, quantity_needed, status, priority_score, affected_people, has_elderly_children, is_medical_emergency, notes, created_at)
SELECT v.id, d.id, 'MEDICAL', 'Immediate inhaler and pediatric emergency check-up required.', 5, 1, 'ASSIGNED', 88, 4, TRUE, TRUE, 'Volunteer nurse assigned.', DATE_SUB(@now, INTERVAL 16 DAY)
FROM victims v JOIN disasters d ON d.id = v.disaster_id
WHERE v.full_name = 'Karthik Elangovan' AND d.title = 'Chennai Cyclone Surge - Marina Belt'
  AND NOT EXISTS (SELECT 1 FROM relief_requests r WHERE r.victim_id = v.id AND r.request_type = 'MEDICAL');

INSERT INTO relief_requests (victim_id, disaster_id, request_type, description, urgency_level, quantity_needed, status, priority_score, affected_people, has_elderly_children, is_medical_emergency, notes, created_at)
SELECT v.id, d.id, 'SHELTER', 'Emergency temporary shelter for family of five including pregnant woman.', 4, 1, 'PENDING', 72, 5, TRUE, TRUE, 'Awaiting nearby shelter capacity confirmation.', DATE_SUB(@now, INTERVAL 9 DAY)
FROM victims v JOIN disasters d ON d.id = v.disaster_id
WHERE v.full_name = 'Rahima Begum' AND d.title = 'Guwahati Flash Flood - Beltola'
  AND NOT EXISTS (SELECT 1 FROM relief_requests r WHERE r.victim_id = v.id AND r.request_type = 'SHELTER');

INSERT INTO relief_requests (victim_id, disaster_id, request_type, description, urgency_level, quantity_needed, status, priority_score, affected_people, has_elderly_children, is_medical_emergency, notes, created_at)
SELECT v.id, d.id, 'RESCUE', 'Boat evacuation needed — road access blocked by high water. Wheelchair requires flat rescue platform.', 5, 1, 'PENDING', 92, 7, TRUE, FALSE, 'Road access blocked; priority evacuation.', DATE_SUB(@now, INTERVAL 4 DAY)
FROM victims v JOIN disasters d ON d.id = v.disaster_id
WHERE v.full_name = 'Prasenjit Bora' AND d.title = 'Dibrugarh Embankment Breach Response'
  AND NOT EXISTS (SELECT 1 FROM relief_requests r WHERE r.victim_id = v.id AND r.request_type = 'RESCUE');

INSERT INTO relief_requests (victim_id, disaster_id, request_type, description, urgency_level, quantity_needed, status, priority_score, affected_people, has_elderly_children, is_medical_emergency, notes, created_at)
SELECT v.id, d.id, 'MEDICAL', 'Insulin supply and emergency check-up for elderly diabetic woman.', 5, 2, 'VERIFIED', 85, 3, TRUE, TRUE, 'Critical — insulin-dependent. Urgent.', DATE_SUB(@now, INTERVAL 10 DAY)
FROM victims v JOIN disasters d ON d.id = v.disaster_id
WHERE v.full_name = 'Indira Moorthy' AND d.title = 'Chennai Flooding - Velachery Basin'
  AND NOT EXISTS (SELECT 1 FROM relief_requests r WHERE r.victim_id = v.id AND r.request_type = 'MEDICAL');

INSERT INTO relief_requests (victim_id, disaster_id, request_type, description, urgency_level, quantity_needed, status, priority_score, affected_people, has_elderly_children, is_medical_emergency, notes, created_at)
SELECT v.id, d.id, 'FOOD', 'Dry food rations for 8-member family including two infants.', 3, 24, 'SUBMITTED', 55, 8, TRUE, FALSE, 'Family in open ground; no cooking facility.', DATE_SUB(@now, INTERVAL 5 DAY)
FROM victims v JOIN disasters d ON d.id = v.disaster_id
WHERE v.full_name = 'Javed Ansari' AND d.title = 'Kolkata Riverbank Flood Alert'
  AND NOT EXISTS (SELECT 1 FROM relief_requests r WHERE r.victim_id = v.id AND r.request_type = 'FOOD');

-- =============================================================
-- 10. ASSIGNMENTS
-- =============================================================
INSERT INTO assignments (volunteer_id, disaster_id, relief_request_id, assigned_by, status, notes, assigned_at, accepted_at, completed_at, hours_logged)
SELECT vol.id, d.id, rr.id, ua.id, 'IN_PROGRESS', 'Water distribution and sanitation awareness at Kurla.',
  DATE_SUB(@now, INTERVAL 21 DAY), DATE_SUB(@now, INTERVAL 21 DAY), NULL, 14.5
FROM volunteers vol
JOIN users uv ON uv.id = vol.user_id
JOIN disasters d ON d.title = 'Mumbai Monsoon Flooding - Kurla Cluster'
JOIN relief_requests rr ON rr.disaster_id = d.id AND rr.request_type = 'WATER'
JOIN users ua ON ua.username = 'ops_lead_mumbai'
WHERE uv.username = 'volunteer_mum_01'
  AND NOT EXISTS (SELECT 1 FROM assignments a WHERE a.volunteer_id = vol.id AND a.relief_request_id = rr.id);

INSERT INTO assignments (volunteer_id, disaster_id, relief_request_id, assigned_by, status, notes, assigned_at, accepted_at, completed_at, hours_logged)
SELECT vol.id, d.id, rr.id, ua.id, 'COMPLETED', 'Medical check-up completed. Inhaler administered. Meds distributed.',
  DATE_SUB(@now, INTERVAL 16 DAY), DATE_SUB(@now, INTERVAL 16 DAY), DATE_SUB(@now, INTERVAL 14 DAY), 11.0
FROM volunteers vol
JOIN users uv ON uv.id = vol.user_id
JOIN disasters d ON d.title = 'Chennai Cyclone Surge - Marina Belt'
JOIN relief_requests rr ON rr.disaster_id = d.id AND rr.request_type = 'MEDICAL'
JOIN users ua ON ua.username = 'ops_lead_chennai'
WHERE uv.username = 'volunteer_che_01'
  AND NOT EXISTS (SELECT 1 FROM assignments a WHERE a.volunteer_id = vol.id AND a.relief_request_id = rr.id);

INSERT INTO assignments (volunteer_id, disaster_id, relief_request_id, assigned_by, status, notes, assigned_at, accepted_at, completed_at, hours_logged)
SELECT vol.id, d.id, rr.id, ua.id, 'ASSIGNED', 'Rapid boat rescue support with local flotilla. Await clear weather.',
  DATE_SUB(@now, INTERVAL 3 DAY), NULL, NULL, NULL
FROM volunteers vol
JOIN users uv ON uv.id = vol.user_id
JOIN disasters d ON d.title = 'Dibrugarh Embankment Breach Response'
JOIN relief_requests rr ON rr.disaster_id = d.id AND rr.request_type = 'RESCUE'
JOIN users ua ON ua.username = 'admin'
WHERE uv.username = 'volunteer_asm_01'
  AND NOT EXISTS (SELECT 1 FROM assignments a WHERE a.volunteer_id = vol.id AND a.relief_request_id = rr.id);

-- Responder assignments
INSERT INTO assignments (volunteer_id, disaster_id, relief_request_id, assigned_by, status, notes, assigned_at, accepted_at, completed_at, hours_logged)
SELECT vol.id, d.id, rr.id, ua.id, 'ACCEPTED', 'NDRF boat team deployment confirmed. Medical team on standby.',
  DATE_SUB(@now, INTERVAL 2 DAY), DATE_SUB(@now, INTERVAL 2 DAY), NULL, NULL
FROM volunteers vol
JOIN users uv ON uv.id = vol.user_id
JOIN disasters d ON d.title = 'Dibrugarh Embankment Breach Response'
JOIN relief_requests rr ON rr.disaster_id = d.id AND rr.request_type = 'RESCUE'
JOIN users ua ON ua.username = 'admin'
WHERE uv.username = 'responder_01'
  AND NOT EXISTS (SELECT 1 FROM assignments a WHERE a.volunteer_id = vol.id AND a.relief_request_id = rr.id);

INSERT INTO assignments (volunteer_id, disaster_id, relief_request_id, assigned_by, status, notes, assigned_at, accepted_at, completed_at, hours_logged)
SELECT vol.id, d.id, rr.id, ua.id, 'COMPLETED', 'Trauma triage completed at Marina shelter. 3 critical cases stabilized.',
  DATE_SUB(@now, INTERVAL 14 DAY), DATE_SUB(@now, INTERVAL 14 DAY), DATE_SUB(@now, INTERVAL 10 DAY), 24.0
FROM volunteers vol
JOIN users uv ON uv.id = vol.user_id
JOIN disasters d ON d.title = 'Chennai Cyclone Surge - Marina Belt'
JOIN relief_requests rr ON rr.disaster_id = d.id AND rr.request_type = 'MEDICAL'
JOIN users ua ON ua.username = 'ops_lead_chennai'
WHERE uv.username = 'responder_03'
  AND NOT EXISTS (SELECT 1 FROM assignments a WHERE a.volunteer_id = vol.id AND a.relief_request_id = rr.id);

-- =============================================================
-- 11. INVENTORY
-- =============================================================
INSERT INTO inventory (item_name, category, quantity, unit, location_id, disaster_id, min_threshold, expiry_date, created_at)
SELECT 'Ready-to-Eat Meal Pack', 'FOOD', 680, 'packs', l.id, d.id, 120, DATE_ADD(CURDATE(), INTERVAL 120 DAY), DATE_SUB(@now, INTERVAL 28 DAY)
FROM locations l JOIN disasters d ON d.title = 'Mumbai Monsoon Flooding - Kurla Cluster'
WHERE l.name = 'Navi Mumbai Distribution Hub'
  AND NOT EXISTS (SELECT 1 FROM inventory i WHERE i.item_name = 'Ready-to-Eat Meal Pack' AND i.location_id = l.id AND i.disaster_id = d.id);

INSERT INTO inventory (item_name, category, quantity, unit, location_id, disaster_id, min_threshold, expiry_date, created_at)
SELECT 'Drinking Water 20L Can', 'WATER', 74, 'cans', l.id, d.id, 100, DATE_ADD(CURDATE(), INTERVAL 180 DAY), DATE_SUB(@now, INTERVAL 27 DAY)
FROM locations l JOIN disasters d ON d.title = 'Mumbai Monsoon Flooding - Kurla Cluster'
WHERE l.name = 'Mumbai Relief Camp - Sion'
  AND NOT EXISTS (SELECT 1 FROM inventory i WHERE i.item_name = 'Drinking Water 20L Can' AND i.location_id = l.id AND i.disaster_id = d.id);

INSERT INTO inventory (item_name, category, quantity, unit, location_id, disaster_id, min_threshold, expiry_date, created_at)
SELECT 'Chlorine Tablets', 'MEDICINE', 45, 'boxes', l.id, d.id, 80, DATE_ADD(CURDATE(), INTERVAL 365 DAY), DATE_SUB(@now, INTERVAL 27 DAY)
FROM locations l JOIN disasters d ON d.title = 'Mumbai Monsoon Flooding - Kurla Cluster'
WHERE l.name = 'Mumbai Shelter - Dharavi School'
  AND NOT EXISTS (SELECT 1 FROM inventory i WHERE i.item_name = 'Chlorine Tablets' AND i.location_id = l.id AND i.disaster_id = d.id);

INSERT INTO inventory (item_name, category, quantity, unit, location_id, disaster_id, min_threshold, expiry_date, created_at)
SELECT 'Emergency Blanket', 'CLOTHING', 320, 'pieces', l.id, d.id, 90, NULL, DATE_SUB(@now, INTERVAL 20 DAY)
FROM locations l JOIN disasters d ON d.title = 'Chennai Cyclone Surge - Marina Belt'
WHERE l.name = 'Chennai Shelter - Velachery'
  AND NOT EXISTS (SELECT 1 FROM inventory i WHERE i.item_name = 'Emergency Blanket' AND i.location_id = l.id AND i.disaster_id = d.id);

INSERT INTO inventory (item_name, category, quantity, unit, location_id, disaster_id, min_threshold, expiry_date, created_at)
SELECT 'ORS Sachets', 'MEDICINE', 210, 'sachets', l.id, d.id, 60, DATE_ADD(CURDATE(), INTERVAL 300 DAY), DATE_SUB(@now, INTERVAL 19 DAY)
FROM locations l JOIN disasters d ON d.title = 'Chennai Cyclone Surge - Marina Belt'
WHERE l.name = 'Chennai Medical Camp - Perungudi'
  AND NOT EXISTS (SELECT 1 FROM inventory i WHERE i.item_name = 'ORS Sachets' AND i.location_id = l.id AND i.disaster_id = d.id);

INSERT INTO inventory (item_name, category, quantity, unit, location_id, disaster_id, min_threshold, expiry_date, created_at)
SELECT 'Inflatable Rescue Boat Fuel', 'EQUIPMENT', 18, 'drums', l.id, d.id, 30, NULL, DATE_SUB(@now, INTERVAL 4 DAY)
FROM locations l JOIN disasters d ON d.title = 'Dibrugarh Embankment Breach Response'
WHERE l.name = 'Guwahati Shelter - Beltola'
  AND NOT EXISTS (SELECT 1 FROM inventory i WHERE i.item_name = 'Inflatable Rescue Boat Fuel' AND i.location_id = l.id AND i.disaster_id = d.id);

INSERT INTO inventory (item_name, category, quantity, unit, location_id, disaster_id, min_threshold, expiry_date, created_at)
SELECT 'Life Jackets', 'EQUIPMENT', 22, 'units', l.id, d.id, 25, NULL, DATE_SUB(@now, INTERVAL 3 DAY)
FROM locations l JOIN disasters d ON d.title = 'Dibrugarh Embankment Breach Response'
WHERE l.name = 'Guwahati Shelter - Beltola'
  AND NOT EXISTS (SELECT 1 FROM inventory i WHERE i.item_name = 'Life Jackets' AND i.location_id = l.id AND i.disaster_id = d.id);

INSERT INTO inventory (item_name, category, quantity, unit, location_id, disaster_id, min_threshold, expiry_date, created_at)
SELECT 'Field Medical Kit', 'MEDICINE', 12, 'kits', l.id, d.id, 20, DATE_ADD(CURDATE(), INTERVAL 730 DAY), DATE_SUB(@now, INTERVAL 5 DAY)
FROM locations l JOIN disasters d ON d.title = 'Kolkata Riverbank Flood Alert'
WHERE l.name = 'Kolkata Relief Camp - Howrah'
  AND NOT EXISTS (SELECT 1 FROM inventory i WHERE i.item_name = 'Field Medical Kit' AND i.location_id = l.id AND i.disaster_id = d.id);

INSERT INTO inventory (item_name, category, quantity, unit, location_id, disaster_id, min_threshold, expiry_date, created_at)
SELECT 'Tarpaulin Shelter Sheets', 'SHELTER', 145, 'pieces', l.id, d.id, 50, NULL, DATE_SUB(@now, INTERVAL 6 DAY)
FROM locations l JOIN disasters d ON d.title = 'Kolkata Riverbank Flood Alert'
WHERE l.name = 'Kolkata Relief Camp - Howrah'
  AND NOT EXISTS (SELECT 1 FROM inventory i WHERE i.item_name = 'Tarpaulin Shelter Sheets' AND i.location_id = l.id AND i.disaster_id = d.id);

INSERT INTO inventory (item_name, category, quantity, unit, location_id, disaster_id, min_threshold, expiry_date, created_at)
SELECT 'Water Tanker (5000L)', 'WATER', 3, 'vehicles', l.id, d.id, 2, NULL, DATE_SUB(@now, INTERVAL 30 DAY)
FROM locations l JOIN disasters d ON d.title = 'Rajasthan Drought Relief - Barmer Region'
WHERE l.name = 'Delhi Relief Hub - Sarai Kale Khan'
  AND NOT EXISTS (SELECT 1 FROM inventory i WHERE i.item_name = 'Water Tanker (5000L)' AND i.location_id = l.id AND i.disaster_id = d.id);

-- =============================================================
-- 12. PAYMENTS + DONATIONS
-- =============================================================
INSERT INTO payments (user_id, amount, currency, status, provider, payment_order_id, payment_id, donor_name, donor_email, donor_phone, created_at)
SELECT u.id, 150000.00, 'INR', 'CAPTURED', 'RAZORPAY', 'order_demo_2026_001', 'pay_demo_2026_001', 'Suryanet Logistics Pvt Ltd', 'donor.corp01@example.com', '9000000041', DATE_SUB(@now, INTERVAL 20 DAY)
FROM users u WHERE u.username = 'donor_corp_01'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE payment_order_id = 'order_demo_2026_001');

INSERT INTO payments (user_id, amount, currency, status, provider, payment_order_id, payment_id, donor_name, donor_email, donor_phone, created_at)
SELECT u.id, 25000.00, 'INR', 'CAPTURED', 'RAZORPAY', 'order_demo_2026_002', 'pay_demo_2026_002', 'Neha Kulkarni', 'donor.ind01@example.com', '9000000042', DATE_SUB(@now, INTERVAL 14 DAY)
FROM users u WHERE u.username = 'donor_ind_01'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE payment_order_id = 'order_demo_2026_002');

INSERT INTO payments (user_id, amount, currency, status, provider, payment_order_id, payment_id, donor_name, donor_email, donor_phone, failure_reason, created_at)
SELECT u.id, 10000.00, 'INR', 'FAILED', 'RAZORPAY', 'order_demo_2026_003', 'pay_demo_2026_003', 'Neha Kulkarni', 'donor.ind01@example.com', '9000000042', 'UPI timeout during bank confirmation', DATE_SUB(@now, INTERVAL 5 DAY)
FROM users u WHERE u.username = 'donor_ind_01'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE payment_order_id = 'order_demo_2026_003');

INSERT INTO payments (user_id, amount, currency, status, provider, payment_order_id, payment_id, donor_name, donor_email, donor_phone, created_at)
SELECT u.id, 75000.00, 'INR', 'CAPTURED', 'RAZORPAY', 'order_demo_2026_004', 'pay_demo_2026_004', 'Bharat Tech Solutions', 'donor.corp02@example.com', '9000000043', DATE_SUB(@now, INTERVAL 10 DAY)
FROM users u WHERE u.username = 'donor_corp_02'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE payment_order_id = 'order_demo_2026_004');

INSERT INTO payments (user_id, amount, currency, status, provider, payment_order_id, payment_id, donor_name, donor_email, donor_phone, created_at)
SELECT u.id, 200000.00, 'INR', 'CAPTURED', 'RAZORPAY', 'order_demo_2026_005', 'pay_demo_2026_005', 'Greenpath Foundation', 'donor.corp03@example.com', '9000000044', DATE_SUB(@now, INTERVAL 6 DAY)
FROM users u WHERE u.username = 'donor_corp_03'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE payment_order_id = 'order_demo_2026_005');

INSERT INTO payments (user_id, amount, currency, status, provider, payment_order_id, payment_id, donor_name, donor_email, donor_phone, created_at)
SELECT u.id, 15000.00, 'INR', 'CAPTURED', 'RAZORPAY', 'order_demo_2026_006', 'pay_demo_2026_006', 'Arjun Singhvi', 'donor.ind02@example.com', '9000000045', DATE_SUB(@now, INTERVAL 2 DAY)
FROM users u WHERE u.username = 'donor_ind_02'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE payment_order_id = 'order_demo_2026_006');

-- Donations
INSERT INTO donations (payment_id, disaster_id, amount, donation_type, description, status, donor_name, donor_email, donor_phone, is_anonymous, created_at)
SELECT p.id, d.id, 150000.00, 'MONETARY', 'Corporate relief fund for flood and shelter operations.',
  'CONFIRMED', 'Suryanet Logistics Pvt Ltd', 'donor.corp01@example.com', '9000000041', FALSE, DATE_SUB(@now, INTERVAL 20 DAY)
FROM payments p JOIN disasters d ON d.title = 'Mumbai Monsoon Flooding - Kurla Cluster'
WHERE p.payment_order_id = 'order_demo_2026_001'
  AND NOT EXISTS (SELECT 1 FROM donations dn WHERE dn.payment_id = p.id);

INSERT INTO donations (payment_id, disaster_id, amount, donation_type, description, status, donor_name, donor_email, donor_phone, is_anonymous, created_at)
SELECT p.id, d.id, 25000.00, 'MONETARY', 'Emergency medical assistance contribution.',
  'CONFIRMED', 'Neha Kulkarni', 'donor.ind01@example.com', '9000000042', FALSE, DATE_SUB(@now, INTERVAL 14 DAY)
FROM payments p JOIN disasters d ON d.title = 'Chennai Cyclone Surge - Marina Belt'
WHERE p.payment_order_id = 'order_demo_2026_002'
  AND NOT EXISTS (SELECT 1 FROM donations dn WHERE dn.payment_id = p.id);

INSERT INTO donations (payment_id, disaster_id, amount, donation_type, description, status, donor_name, donor_email, donor_phone, is_anonymous, created_at)
SELECT p.id, d.id, 75000.00, 'MONETARY', 'Tech company flood relief — water tankers and equipment.',
  'CONFIRMED', 'Bharat Tech Solutions', 'donor.corp02@example.com', '9000000043', FALSE, DATE_SUB(@now, INTERVAL 10 DAY)
FROM payments p JOIN disasters d ON d.title = 'Dibrugarh Embankment Breach Response'
WHERE p.payment_order_id = 'order_demo_2026_004'
  AND NOT EXISTS (SELECT 1 FROM donations dn WHERE dn.payment_id = p.id);

INSERT INTO donations (payment_id, disaster_id, amount, donation_type, description, status, donor_name, donor_email, donor_phone, is_anonymous, created_at)
SELECT p.id, d.id, 200000.00, 'MONETARY', 'Greenpath Foundation relief endowment for multi-zone operations.',
  'CONFIRMED', 'Greenpath Foundation', 'donor.corp03@example.com', '9000000044', FALSE, DATE_SUB(@now, INTERVAL 6 DAY)
FROM payments p JOIN disasters d ON d.title = 'Kolkata Riverbank Flood Alert'
WHERE p.payment_order_id = 'order_demo_2026_005'
  AND NOT EXISTS (SELECT 1 FROM donations dn WHERE dn.payment_id = p.id);

INSERT INTO donations (payment_id, disaster_id, amount, donation_type, description, status, donor_name, donor_email, donor_phone, is_anonymous, created_at)
SELECT p.id, d.id, 15000.00, 'MONETARY', 'Individual contribution for Kolkata flood relief.',
  'CONFIRMED', 'Arjun Singhvi', 'donor.ind02@example.com', '9000000045', FALSE, DATE_SUB(@now, INTERVAL 2 DAY)
FROM payments p JOIN disasters d ON d.title = 'Guwahati Flash Flood - Beltola'
WHERE p.payment_order_id = 'order_demo_2026_006'
  AND NOT EXISTS (SELECT 1 FROM donations dn WHERE dn.payment_id = p.id);

-- Payment events
INSERT INTO payment_events (payment_id, event_type, payload, processed, created_at)
SELECT p.id, 'payment.captured', JSON_OBJECT('source', 'seed_demo', 'note', 'captured'), TRUE, DATE_SUB(@now, INTERVAL 20 DAY)
FROM payments p WHERE p.payment_order_id = 'order_demo_2026_001'
  AND NOT EXISTS (SELECT 1 FROM payment_events pe WHERE pe.payment_id = p.id AND pe.event_type = 'payment.captured');

INSERT INTO payment_events (payment_id, event_type, payload, processed, created_at)
SELECT p.id, 'payment.failed', JSON_OBJECT('source', 'seed_demo', 'note', 'failed'), TRUE, DATE_SUB(@now, INTERVAL 5 DAY)
FROM payments p WHERE p.payment_order_id = 'order_demo_2026_003'
  AND NOT EXISTS (SELECT 1 FROM payment_events pe WHERE pe.payment_id = p.id AND pe.event_type = 'payment.failed');

-- =============================================================
-- 13. NOTIFICATIONS
-- =============================================================
INSERT INTO notifications (user_id, title, message, notification_type, reference_type, reference_id, is_read, created_at)
SELECT u.id, 'High Priority Flood Escalation', 'Kurla flood moved to high-priority response. Report every 2 hours.', 'DISASTER_ALERT', 'DISASTER', d.id, FALSE, DATE_SUB(@now, INTERVAL 23 DAY)
FROM users u JOIN disasters d ON d.title = 'Mumbai Monsoon Flooding - Kurla Cluster'
WHERE u.username = 'ops_lead_mumbai'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'High Priority Flood Escalation' AND n.reference_id = d.id);

INSERT INTO notifications (user_id, title, message, notification_type, reference_type, reference_id, is_read, created_at)
SELECT u.id, 'Medical Request Assigned', 'Urgent pediatric medical request at Marina shelter assigned to you.', 'ASSIGNMENT', 'RELIEF_REQUEST', rr.id, TRUE, DATE_SUB(@now, INTERVAL 16 DAY)
FROM users u
JOIN volunteers v ON v.user_id = u.id
JOIN disasters d ON d.title = 'Chennai Cyclone Surge - Marina Belt'
JOIN relief_requests rr ON rr.disaster_id = d.id AND rr.request_type = 'MEDICAL'
WHERE u.username = 'volunteer_che_01'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'Medical Request Assigned');

INSERT INTO notifications (user_id, title, message, notification_type, reference_type, reference_id, is_read, created_at)
SELECT u.id, 'Inventory Shortage Alert', 'Drinking water stock is critically low at Mumbai Relief Camp - Sion.', 'INVENTORY_ALERT', 'INVENTORY', i.id, FALSE, DATE_SUB(@now, INTERVAL 2 DAY)
FROM users u
JOIN locations l ON l.name = 'Mumbai Relief Camp - Sion'
JOIN inventory i ON i.location_id = l.id AND i.item_name = 'Drinking Water 20L Can'
WHERE u.username = 'ops_lead_mumbai'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'Inventory Shortage Alert' AND n.reference_id = i.id);

INSERT INTO notifications (user_id, title, message, notification_type, reference_type, reference_id, is_read, created_at)
SELECT u.id, 'Donation Received — INR 1,50,000', 'Corporate donation from Suryanet Logistics confirmed for Mumbai flood response.', 'DONATION', 'DONATION', dn.id, FALSE, DATE_SUB(@now, INTERVAL 20 DAY)
FROM users u JOIN donations dn ON dn.donor_email = 'donor.corp01@example.com'
WHERE u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'Donation Received — INR 1,50,000');

INSERT INTO notifications (user_id, title, message, notification_type, reference_type, reference_id, is_read, created_at)
SELECT u.id, 'New NDRF Assignment — Dibrugarh', 'NDRF boat team deployment confirmed for embankment breach response.', 'ASSIGNMENT', 'DISASTER', d.id, FALSE, DATE_SUB(@now, INTERVAL 2 DAY)
FROM users u JOIN disasters d ON d.title = 'Dibrugarh Embankment Breach Response'
WHERE u.username = 'responder_01'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'New NDRF Assignment — Dibrugarh');

-- =============================================================
-- 14. NEWS FEED (rich demo articles)
-- =============================================================
INSERT INTO news_updates (title, summary, content, image_url, disaster_type, severity, status, location, latitude, longitude, source_incident_id, affected_people, rescue_progress, created_by, created_at, updated_at)
SELECT
  'Kurla Flooding Intensifies: Evacuation Extended Overnight',
  'Rescue corridors expanded in Kurla and Sion as flood levels remained high. Boats operating round-the-clock.',
  'Command teams confirmed additional boats and medical units were dispatched to low-lying pockets of Kurla West and Sion North. Emergency shelters at Sion Ground and Dharavi Municipal School have been expanded with food and clean water support. Over 3,200 residents have been relocated from ground-floor and basement units. Authorities continue to monitor storm drain overflow at key road junctions. Power supply to 11 wards remains disrupted. NDRF teams from Pune battalion arrived at 03:00 hrs to assist with high-water extractions. Families with mobility-impaired members are being prioritized in the next extraction wave.',
  'https://images.unsplash.com/photo-1454789548928-9efd52dc4031?auto=format&fit=crop&w=1400&q=80',
  'Flood', 'CRITICAL', 'ACTIVE', 'Kurla, Mumbai', 19.0735, 72.8790,
  d.id, 3200, 42, u.id, DATE_SUB(@now, INTERVAL 23 DAY), DATE_SUB(@now, INTERVAL 2 HOUR)
FROM disasters d, users u
WHERE d.title = 'Mumbai Monsoon Flooding - Kurla Cluster' AND u.username = 'ops_lead_mumbai'
  AND NOT EXISTS (SELECT 1 FROM news_updates n WHERE n.title = 'Kurla Flooding Intensifies: Evacuation Extended Overnight');

INSERT INTO news_updates (title, summary, content, image_url, disaster_type, severity, status, location, latitude, longitude, source_incident_id, affected_people, rescue_progress, created_by, created_at, updated_at)
SELECT
  'Chennai Cyclone Surge: Coastal Shelters Stable, Warnings Extended',
  'Shelter occupancy controlled as coastal teams monitor wind spikes. Category 3 warning remains in effect for 48 hours.',
  'The operations desk at Chennai SDRF reports stable occupancy across Velachery, Perungudi, and Foreshore Estate shelters. Over 5,100 residents have been evacuated from coastal zones. Medical response teams remain on standby at all camp entry points. Utility crews are working to restore localized electricity outages affecting 7 wards. Public advisories remain active for vulnerable coastal wards. Rainfall is forecast to intensify overnight. Additional food supplies have been airlifted to Marina beach relief camp. Citizens are advised against return to coastal zones until the all-clear signal is issued by the district administration.',
  'https://images.unsplash.com/photo-1527489377706-5bf97e608852?auto=format&fit=crop&w=1400&q=80',
  'Cyclone', 'CRITICAL', 'ACTIVE', 'Marina Belt, Chennai', 13.0499, 80.2824,
  d.id, 5100, 55, u.id, DATE_SUB(@now, INTERVAL 17 DAY), DATE_SUB(@now, INTERVAL 4 HOUR)
FROM disasters d, users u
WHERE d.title = 'Chennai Cyclone Surge - Marina Belt' AND u.username = 'ops_lead_chennai'
  AND NOT EXISTS (SELECT 1 FROM news_updates n WHERE n.title = 'Chennai Cyclone Surge: Coastal Shelters Stable, Warnings Extended');

INSERT INTO news_updates (title, summary, content, image_url, disaster_type, severity, status, location, latitude, longitude, source_incident_id, affected_people, rescue_progress, created_by, created_at, updated_at)
SELECT
  'Navi Mumbai Heatwave Response Formally Closed',
  'All heatwave emergency operations wound down. Baseline health metrics restored across affected wards.',
  'District teams confirmed that emergency medical demand has returned to baseline across Nerul, Airoli, and Vashi sectors. Cooling centers that served over 850 residents have been formally closed after 7 operational days. Post-incident health monitoring will continue for the next 72 hours through district health workers. Three facilities have been designated as ongoing heat-advisory referral centers for the summer. MSEDCL has completed power supply audits at all facilities that were used as cooling points.',
  'https://images.unsplash.com/photo-1500375592092-40eb2168fd21?auto=format&fit=crop&w=1400&q=80',
  'Heatwave', 'MEDIUM', 'RESOLVED', 'Nerul, Navi Mumbai', 19.0330, 73.0297,
  d.id, 850, 100, u.id, DATE_SUB(@now, INTERVAL 48 DAY), DATE_SUB(@now, INTERVAL 22 DAY)
FROM disasters d, users u
WHERE d.title = 'Navi Mumbai Heatwave Impact' AND u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM news_updates n WHERE n.title = 'Navi Mumbai Heatwave Response Formally Closed');

INSERT INTO news_updates (title, summary, content, image_url, disaster_type, severity, status, location, latitude, longitude, source_incident_id, affected_people, rescue_progress, created_by, created_at, updated_at)
SELECT
  'Dibrugarh Breach: NDRF Boats Deployed, Rescue at 40%',
  'NDRF Battalion 3 has deployed 6 rescue boats along embankment breach zone. Aerial food drops initiated for cut-off villages.',
  'The National Disaster Response Force confirmed six inflatable boats are operational along the Lohit tributary breach. Villages of Lahowal, Barbarua, and Tingkhong remain cut off by road. Rescue operations have extracted 960 individuals from the 2,400 affected zone, representing approximately 40% extraction progress. Aerial food drops by state helicopter units have begun for communities unreachable by boat. Fuel shortage for rescue boats remains a critical constraint — emergency resupply dispatched from Guwahati. All extracted residents are being taken to Dibrugarh Town High School shelter. Medical teams are treating respiratory and waterborne illness cases.',
  'https://images.unsplash.com/photo-1547683905-f686c993aae5?auto=format&fit=crop&w=1400&q=80',
  'Flood', 'CRITICAL', 'ACTIVE', 'Dibrugarh, Assam', 27.4728, 94.9120,
  d.id, 2400, 40, u.id, DATE_SUB(@now, INTERVAL 4 DAY), DATE_SUB(@now, INTERVAL 1 HOUR)
FROM disasters d, users u
WHERE d.title = 'Dibrugarh Embankment Breach Response' AND u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM news_updates n WHERE n.title = 'Dibrugarh Breach: NDRF Boats Deployed, Rescue at 40%');

INSERT INTO news_updates (title, summary, content, image_url, disaster_type, severity, status, location, latitude, longitude, source_incident_id, affected_people, rescue_progress, created_by, created_at, updated_at)
SELECT
  'Kolkata Flood Alert: Hooghly Levels Critical, North Wards Evacuated',
  'District administration issued mandatory evacuation for 4 north Kolkata wards. Pumping stations at full capacity.',
  'KMC issued mandatory evacuation orders for wards 3, 7, 8, and 11 along the north Hooghly embankment. Over 1,300 residents are relocating to Howrah Maidan Relief Camp and the Rabindra Sarani community center. All pumping stations are operating at full capacity. The state disaster management authority has placed additional SDRF units on standby. Ferries and small boats have been suspended on the Hooghly stretch between Howrah and Dakshineswar. Citizens are urged to report anyone left behind to helpline 1070.',
  'https://images.unsplash.com/photo-1586500035744-6ce0a9b1b25a?auto=format&fit=crop&w=1400&q=80',
  'Flood', 'HIGH', 'ACTIVE', 'North Kolkata, West Bengal', 22.5726, 88.3639,
  d.id, 1300, 28, u.id, DATE_SUB(@now, INTERVAL 6 DAY), DATE_SUB(@now, INTERVAL 8 HOUR)
FROM disasters d, users u
WHERE d.title = 'Kolkata Riverbank Flood Alert' AND u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM news_updates n WHERE n.title = 'Kolkata Flood Alert: Hooghly Levels Critical, North Wards Evacuated');

INSERT INTO news_updates (title, summary, content, image_url, disaster_type, severity, status, location, latitude, longitude, source_incident_id, affected_people, rescue_progress, created_by, created_at, updated_at)
SELECT
  'Delhi Okhla Chemical Spill: Containment Complete, Residual Monitoring',
  'Hazmat team contained the chlorine gas leak within 3 hours. Area cleared; health monitoring ongoing.',
  'NDRF Hazmat Unit confirmed full containment of the chlorine-compound spill on NH-19 near Okhla Industrial Area at 14:45 hrs. Approximately 620 residents from nearby residential blocks were evacuated as a precaution. Air quality monitoring stations report returning to safe levels. 14 individuals were treated for mild respiratory irritation at AIIMS Delhi emergency ward; all discharged by evening. Road traffic on the affected 2.3 km stretch has been reopened. The Ministry of Environment has dispatched an inspection team to assess environmental impact. Residents can return to their homes.',
  'https://images.unsplash.com/photo-1611117775350-ac3950990985?auto=format&fit=crop&w=1400&q=80',
  'Chemical Spill', 'HIGH', 'RESOLVED', 'Okhla, New Delhi', 28.5510, 77.2698,
  d.id, 620, 100, u.id, DATE_SUB(@now, INTERVAL 13 DAY), DATE_SUB(@now, INTERVAL 7 DAY)
FROM disasters d, users u
WHERE d.title = 'Delhi Industrial Chemical Spill - Okhla' AND u.username = 'ops_lead_delhi'
  AND NOT EXISTS (SELECT 1 FROM news_updates n WHERE n.title = 'Delhi Okhla Chemical Spill: Containment Complete, Residual Monitoring');

INSERT INTO news_updates (title, summary, content, image_url, disaster_type, severity, status, location, latitude, longitude, source_incident_id, affected_people, rescue_progress, created_by, created_at, updated_at)
SELECT
  'Rajasthan Drought: Emergency Tanker Operations Reach 8 Villages',
  'State government deploys 3 water tankers to drought-stricken Barmer. NGO teams distribute 7,200 water pouches.',
  'The Rajasthan Water Board and district relief committee have confirmed emergency water tanker operations are now covering all 8 targeted villages in Barmer''s affected zone. Each village is receiving a daily 5,000-litre tanker delivery. NGO partner teams have distributed over 7,200 water pouches as a bridge measure. Borewell drilling has commenced at 3 sites; results expected within 12 days. The district administration has identified cattle water points as a secondary crisis and is deploying separate animal husbandry units. Crop damage assessment is ongoing — initial estimates indicate 68% loss in the winter crop cycle. Relief camp at Barmer town ground remains open for displaced families.',
  'https://images.unsplash.com/photo-1449824913935-59a10b8d2000?auto=format&fit=crop&w=1400&q=80',
  'Drought', 'MEDIUM', 'ACTIVE', 'Barmer, Rajasthan', 25.7456, 71.3934,
  d.id, 4200, 30, u.id, DATE_SUB(@now, INTERVAL 28 DAY), DATE_SUB(@now, INTERVAL 3 DAY)
FROM disasters d, users u
WHERE d.title = 'Rajasthan Drought Relief - Barmer Region' AND u.username = 'ops_lead_delhi'
  AND NOT EXISTS (SELECT 1 FROM news_updates n WHERE n.title = 'Rajasthan Drought: Emergency Tanker Operations Reach 8 Villages');

INSERT INTO news_updates (title, summary, content, image_url, disaster_type, severity, status, location, latitude, longitude, source_incident_id, affected_people, rescue_progress, created_by, created_at, updated_at)
SELECT
  'Velachery Flooding: Insulin Supply Restored, Medical Team On-Ground',
  'Emergency insulin supply air-dropped to Velachery shelter. Medical team reports 3 critical elderly cases stabilized.',
  'Following an urgent request from the relief camp coordinator, the State Health Department authorized an emergency air-drop of insulin and diabetic medication supplies to the Velachery shelter camp on Wednesday afternoon. Three elderly diabetic residents had been without medication for more than 28 hours. All three have been stabilized by the SDRF medical team. Over 1,900 residents remain in shelter as the Velachery basin remains flooded. Pumping operations have cleared 30% of the waterlogged zone. Authorities expect gradual improvement in the next 48 to 72 hours pending rainfall conditions.',
  'https://images.unsplash.com/photo-1584118624012-df056829fbd0?auto=format&fit=crop&w=1400&q=80',
  'Flood', 'HIGH', 'MONITORING', 'Velachery, Chennai', 12.9815, 80.2180,
  d.id, 1900, 30, u.id, DATE_SUB(@now, INTERVAL 10 DAY), DATE_SUB(@now, INTERVAL 12 HOUR)
FROM disasters d, users u
WHERE d.title = 'Chennai Flooding - Velachery Basin' AND u.username = 'ops_lead_chennai'
  AND NOT EXISTS (SELECT 1 FROM news_updates n WHERE n.title = 'Velachery Flooding: Insulin Supply Restored, Medical Team On-Ground');

-- News timeline updates
INSERT INTO news_timeline_updates (news_id, update_text, update_timestamp)
SELECT n.id, 'First public advisory issued to Kurla East and Sion wards.', DATE_SUB(@now, INTERVAL 22 DAY)
FROM news_updates n WHERE n.title = 'Kurla Flooding Intensifies: Evacuation Extended Overnight'
  AND NOT EXISTS (SELECT 1 FROM news_timeline_updates t WHERE t.news_id = n.id AND t.update_text LIKE 'First public advisory%');

INSERT INTO news_timeline_updates (news_id, update_text, update_timestamp)
SELECT n.id, 'Additional evacuation boats deployed to Nehru Nagar and Tilak Nagar corridors.', DATE_SUB(@now, INTERVAL 20 DAY)
FROM news_updates n WHERE n.title = 'Kurla Flooding Intensifies: Evacuation Extended Overnight'
  AND NOT EXISTS (SELECT 1 FROM news_timeline_updates t WHERE t.news_id = n.id AND t.update_text LIKE 'Additional evacuation boats%');

INSERT INTO news_timeline_updates (news_id, update_text, update_timestamp)
SELECT n.id, 'NDRF Pune Battalion arrived and integrated into rescue command.', DATE_SUB(@now, INTERVAL 18 DAY)
FROM news_updates n WHERE n.title = 'Kurla Flooding Intensifies: Evacuation Extended Overnight'
  AND NOT EXISTS (SELECT 1 FROM news_timeline_updates t WHERE t.news_id = n.id AND t.update_text LIKE 'NDRF Pune%');

INSERT INTO news_timeline_updates (news_id, update_text, update_timestamp)
SELECT n.id, 'Category 3 cyclone warning issued. All coastal residents instructed to evacuate.', DATE_SUB(@now, INTERVAL 16 DAY)
FROM news_updates n WHERE n.title = 'Chennai Cyclone Surge: Coastal Shelters Stable, Warnings Extended'
  AND NOT EXISTS (SELECT 1 FROM news_timeline_updates t WHERE t.news_id = n.id AND t.update_text LIKE 'Category 3 cyclone%');

INSERT INTO news_timeline_updates (news_id, update_text, update_timestamp)
SELECT n.id, 'Velachery and Perungudi shelters reached 80% capacity. Overflow camp activated.', DATE_SUB(@now, INTERVAL 14 DAY)
FROM news_updates n WHERE n.title = 'Chennai Cyclone Surge: Coastal Shelters Stable, Warnings Extended'
  AND NOT EXISTS (SELECT 1 FROM news_timeline_updates t WHERE t.news_id = n.id AND t.update_text LIKE 'Velachery and Perungudi%');

INSERT INTO news_timeline_updates (news_id, update_text, update_timestamp)
SELECT n.id, '6 NDRF boats operational. Lahowal and Barbarua villages first extraction target.', DATE_SUB(@now, INTERVAL 3 DAY)
FROM news_updates n WHERE n.title = 'Dibrugarh Breach: NDRF Boats Deployed, Rescue at 40%'
  AND NOT EXISTS (SELECT 1 FROM news_timeline_updates t WHERE t.news_id = n.id AND t.update_text LIKE '6 NDRF boats%');

INSERT INTO news_timeline_updates (news_id, update_text, update_timestamp)
SELECT n.id, 'Aerial food drops completed for Tingkhong and Namruk villages. Boat fuel resupply en route.', DATE_SUB(@now, INTERVAL 1 DAY)
FROM news_updates n WHERE n.title = 'Dibrugarh Breach: NDRF Boats Deployed, Rescue at 40%'
  AND NOT EXISTS (SELECT 1 FROM news_timeline_updates t WHERE t.news_id = n.id AND t.update_text LIKE 'Aerial food drops%');

INSERT INTO news_timeline_updates (news_id, update_text, update_timestamp)
SELECT n.id, 'Mandatory evacuation orders issued for 4 north Kolkata wards.', DATE_SUB(@now, INTERVAL 5 DAY)
FROM news_updates n WHERE n.title = 'Kolkata Flood Alert: Hooghly Levels Critical, North Wards Evacuated'
  AND NOT EXISTS (SELECT 1 FROM news_timeline_updates t WHERE t.news_id = n.id AND t.update_text LIKE 'Mandatory evacuation%');

-- =============================================================
-- 15. AUDIT LOGS
-- =============================================================
INSERT INTO audit_logs (user_id, action, entity_type, entity_id, old_value, new_value, ip_address, user_agent, created_at)
SELECT u.id, 'UPDATE_STATUS', 'DISASTER', d.id,
  JSON_OBJECT('status', 'REPORTED'), JSON_OBJECT('status', 'ACTIVE'),
  '103.86.18.22', 'Mozilla/5.0 DemoOps', DATE_SUB(@now, INTERVAL 24 DAY)
FROM users u JOIN disasters d ON d.title = 'Mumbai Monsoon Flooding - Kurla Cluster'
WHERE u.username = 'ops_lead_mumbai'
  AND NOT EXISTS (SELECT 1 FROM audit_logs a WHERE a.user_id = u.id AND a.entity_type = 'DISASTER' AND a.entity_id = d.id AND a.action = 'UPDATE_STATUS');

INSERT INTO audit_logs (user_id, action, entity_type, entity_id, old_value, new_value, ip_address, user_agent, created_at)
SELECT u.id, 'CREATE', 'ASSIGNMENT', a.id,
  NULL, JSON_OBJECT('status', 'ASSIGNED'),
  '49.37.221.90', 'Mozilla/5.0 FieldCoordinator', DATE_SUB(@now, INTERVAL 3 DAY)
FROM users u
JOIN assignments a ON a.notes = 'Rapid boat rescue support with local flotilla. Await clear weather.'
WHERE u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM audit_logs al WHERE al.user_id = u.id AND al.entity_type = 'ASSIGNMENT' AND al.entity_id = a.id AND al.action = 'CREATE');

INSERT INTO audit_logs (user_id, action, entity_type, entity_id, old_value, new_value, ip_address, user_agent, created_at)
SELECT u.id, 'UPDATE_STATUS', 'RELIEF_REQUEST', rr.id,
  JSON_OBJECT('status', 'PENDING'), JSON_OBJECT('status', 'VERIFIED'),
  '103.86.18.55', 'Mozilla/5.0 ChennaiOps', DATE_SUB(@now, INTERVAL 9 DAY)
FROM users u
JOIN disasters d ON d.title = 'Chennai Flooding - Velachery Basin'
JOIN relief_requests rr ON rr.disaster_id = d.id AND rr.request_type = 'MEDICAL'
WHERE u.username = 'ops_lead_chennai'
  AND NOT EXISTS (SELECT 1 FROM audit_logs al WHERE al.user_id = u.id AND al.entity_type = 'RELIEF_REQUEST' AND al.entity_id = rr.id AND al.action = 'UPDATE_STATUS');

COMMIT;
