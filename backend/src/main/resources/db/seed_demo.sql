USE disaster_relief_db;

START TRANSACTION;

SET @now = NOW();

-- =============================================
-- ROLES (idempotent)
-- =============================================
INSERT IGNORE INTO roles (name, description) VALUES
('ROLE_CITIZEN', 'Citizen reporting incidents and requesting support'),
('ROLE_VOLUNTEER', 'Field volunteer providing relief'),
('ROLE_RESPONDER', 'Professional first responder'),
('ROLE_NGO_COORDINATOR', 'NGO coordinator managing partner operations'),
('ROLE_ADMIN', 'System administrator with operational access'),
('ROLE_SUPER_ADMIN', 'Super administrator with full cross-region access');

-- =============================================
-- USERS (password for all: password123)
-- Includes admins, NGO coordinators, volunteers, and citizens.
-- =============================================
INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'admin', 'admin@disasterrelief.org', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'System Admin', '9000000001', TRUE, DATE_SUB(@now, INTERVAL 58 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'ops_lead_mumbai', 'ops.mumbai@disasterrelief.org', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Ananya Iyer', '9000000011', TRUE, DATE_SUB(@now, INTERVAL 55 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'ops_lead_mumbai');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'ops_lead_chennai', 'ops.chennai@disasterrelief.org', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Vignesh Raman', '9000000012', TRUE, DATE_SUB(@now, INTERVAL 53 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'ops_lead_chennai');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'volunteer_mum_01', 'vol.mum01@disasterrelief.org', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Priya Sharma', '9000000021', TRUE, DATE_SUB(@now, INTERVAL 52 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'volunteer_mum_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'volunteer_mum_02', 'vol.mum02@disasterrelief.org', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Arjun More', '9000000022', TRUE, DATE_SUB(@now, INTERVAL 50 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'volunteer_mum_02');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'volunteer_che_01', 'vol.che01@disasterrelief.org', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Meena Subramanian', '9000000023', TRUE, DATE_SUB(@now, INTERVAL 49 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'volunteer_che_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'volunteer_asm_01', 'vol.asm01@disasterrelief.org', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Rituporna Das', '9000000024', TRUE, DATE_SUB(@now, INTERVAL 47 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'volunteer_asm_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'citizen_mumbai_01', 'citizen.mum01@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Sajid Khan', '9000000031', TRUE, DATE_SUB(@now, INTERVAL 45 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'citizen_mumbai_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'citizen_chennai_01', 'citizen.che01@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Lakshmi Narayanan', '9000000032', TRUE, DATE_SUB(@now, INTERVAL 44 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'citizen_chennai_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'citizen_kolkata_01', 'citizen.kol01@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Debanjan Roy', '9000000033', TRUE, DATE_SUB(@now, INTERVAL 43 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'citizen_kolkata_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'donor_corp_01', 'donor.corp01@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Suryanet Logistics Pvt Ltd', '9000000041', TRUE, DATE_SUB(@now, INTERVAL 42 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'donor_corp_01');

INSERT INTO users (username, email, password, full_name, phone, is_active, created_at)
SELECT 'donor_ind_01', 'donor.ind01@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQV.2U.i', 'Neha Kulkarni', '9000000042', TRUE, DATE_SUB(@now, INTERVAL 40 DAY)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'donor_ind_01');

-- role mappings
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_ADMIN' WHERE u.username = 'admin';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_NGO_COORDINATOR' WHERE u.username IN ('ops_lead_mumbai', 'ops_lead_chennai');

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_VOLUNTEER' WHERE u.username IN ('volunteer_mum_01', 'volunteer_mum_02', 'volunteer_che_01', 'volunteer_asm_01');

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_CITIZEN' WHERE u.username IN ('donor_corp_01', 'donor_ind_01', 'citizen_mumbai_01', 'citizen_chennai_01', 'citizen_kolkata_01');

-- =============================================
-- DISASTER TYPES
-- =============================================
INSERT IGNORE INTO disaster_types (name, description, icon) VALUES
('Flood',          'Flooding due to heavy rainfall or river overflow', 'flood'),
('Cyclone',        'Tropical cyclone with strong winds and rainfall',  'cyclone'),
('Landslide',      'Sudden movement of rock and debris down a slope',  'landslide'),
('Urban Fire',     'Large urban/industrial fire event',                'fire'),
('Heatwave',       'Prolonged extreme heat conditions',                'heatwave'),
('Earthquake',     'Seismic activity causing structural damage',       'earthquake');

-- =============================================
-- LOCATIONS / SHELTERS / HUBS (for map clustering)
-- =============================================
INSERT INTO locations (name, address, city, state, latitude, longitude, location_type, is_active, created_at)
SELECT 'Mumbai Relief Camp - Sion', 'Sion East Grounds', 'Mumbai', 'Maharashtra', 19.0448, 72.8649, 'CAMP', TRUE, DATE_SUB(@now, INTERVAL 33 DAY)
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Mumbai Relief Camp - Sion');

INSERT INTO locations (name, address, city, state, latitude, longitude, location_type, is_active, created_at)
SELECT 'Mumbai Shelter - Dharavi School', 'Dharavi Municipal School', 'Mumbai', 'Maharashtra', 19.0403, 72.8573, 'SHELTER', TRUE, DATE_SUB(@now, INTERVAL 32 DAY)
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Mumbai Shelter - Dharavi School');

INSERT INTO locations (name, address, city, state, latitude, longitude, location_type, is_active, created_at)
SELECT 'Navi Mumbai Distribution Hub', 'Vashi Sector 17', 'Navi Mumbai', 'Maharashtra', 19.0771, 73.0008, 'DISTRIBUTION_CENTER', TRUE, DATE_SUB(@now, INTERVAL 32 DAY)
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Navi Mumbai Distribution Hub');

INSERT INTO locations (name, address, city, state, latitude, longitude, location_type, is_active, created_at)
SELECT 'Chennai Shelter - Velachery', 'Velachery Govt School', 'Chennai', 'Tamil Nadu', 12.9752, 80.2209, 'SHELTER', TRUE, DATE_SUB(@now, INTERVAL 30 DAY)
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Chennai Shelter - Velachery');

INSERT INTO locations (name, address, city, state, latitude, longitude, location_type, is_active, created_at)
SELECT 'Chennai Medical Camp - Perungudi', 'OMR Perungudi', 'Chennai', 'Tamil Nadu', 12.9601, 80.2446, 'HOSPITAL', TRUE, DATE_SUB(@now, INTERVAL 30 DAY)
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Chennai Medical Camp - Perungudi');

INSERT INTO locations (name, address, city, state, latitude, longitude, location_type, is_active, created_at)
SELECT 'Kolkata Relief Camp - Howrah', 'Howrah Maidan', 'Howrah', 'West Bengal', 22.5958, 88.2636, 'CAMP', TRUE, DATE_SUB(@now, INTERVAL 29 DAY)
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Kolkata Relief Camp - Howrah');

INSERT INTO locations (name, address, city, state, latitude, longitude, location_type, is_active, created_at)
SELECT 'Guwahati Shelter - Beltola', 'Beltola High School', 'Guwahati', 'Assam', 26.1161, 91.7972, 'SHELTER', TRUE, DATE_SUB(@now, INTERVAL 28 DAY)
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE name = 'Guwahati Shelter - Beltola');

-- =============================================
-- DISASTERS / INCIDENTS (last 60 days)
-- =============================================
INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Mumbai Monsoon Flooding - Kurla Cluster', 'Continuous rain caused waterlogging and home displacement in Kurla and Sion pockets.', dt.id, 'HIGH', 'ACTIVE', 19.0735, 72.8790, 'Kurla, Mumbai', 18.5, 3200, u.id, DATE_SUB(@now, INTERVAL 24 DAY), NULL, DATE_SUB(@now, INTERVAL 24 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Flood' AND u.username = 'ops_lead_mumbai'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Mumbai Monsoon Flooding - Kurla Cluster');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Mumbai Building Fire - Bhiwandi Warehouse', 'Nighttime warehouse fire affected nearby worker housing and triggered emergency response.', dt.id, 'CRITICAL', 'CONTAINED', 19.2813, 73.0483, 'Bhiwandi, Thane', 4.1, 470, u.id, DATE_SUB(@now, INTERVAL 20 DAY), DATE_SUB(@now, INTERVAL 16 DAY), DATE_SUB(@now, INTERVAL 20 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Urban Fire' AND u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Mumbai Building Fire - Bhiwandi Warehouse');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Navi Mumbai Heatwave Impact', 'Heat index crossed dangerous levels; dehydration and medical aid demand surged.', dt.id, 'MEDIUM', 'RESOLVED', 19.0330, 73.0297, 'Nerul, Navi Mumbai', 12.0, 850, u.id, DATE_SUB(@now, INTERVAL 41 DAY), DATE_SUB(@now, INTERVAL 34 DAY), DATE_SUB(@now, INTERVAL 41 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Heatwave' AND u.username = 'ops_lead_mumbai'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Navi Mumbai Heatwave Impact');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Chennai Cyclone Surge - Marina Belt', 'Cyclonic winds and surge flooding affected coastal settlements and transport corridors.', dt.id, 'CRITICAL', 'ACTIVE', 13.0499, 80.2824, 'Marina - Foreshore Estate, Chennai', 27.2, 5100, u.id, DATE_SUB(@now, INTERVAL 18 DAY), NULL, DATE_SUB(@now, INTERVAL 18 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Cyclone' AND u.username = 'ops_lead_chennai'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Chennai Cyclone Surge - Marina Belt');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Chennai Flooding - Velachery Basin', 'Urban flooding due to drainage overflow and intense overnight rainfall.', dt.id, 'HIGH', 'ACTIVE', 12.9815, 80.2180, 'Velachery, Chennai', 11.6, 1900, u.id, DATE_SUB(@now, INTERVAL 12 DAY), NULL, DATE_SUB(@now, INTERVAL 12 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Flood' AND u.username = 'ops_lead_chennai'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Chennai Flooding - Velachery Basin');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Cuddalore Landslide Trigger', 'Slope failure after heavy rain blocked roads and isolated hamlets.', dt.id, 'MEDIUM', 'CONTAINED', 11.7447, 79.7680, 'Cuddalore District', 7.8, 320, u.id, DATE_SUB(@now, INTERVAL 26 DAY), DATE_SUB(@now, INTERVAL 22 DAY), DATE_SUB(@now, INTERVAL 26 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Landslide' AND u.username = 'ops_lead_chennai'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Cuddalore Landslide Trigger');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Kolkata Riverbank Flood Alert', 'Hooghly water level rise affected low-lying wards and temporary settlements.', dt.id, 'HIGH', 'REPORTED', 22.5726, 88.3639, 'Kolkata North Riverbank', 9.4, 1300, u.id, DATE_SUB(@now, INTERVAL 7 DAY), NULL, DATE_SUB(@now, INTERVAL 7 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Flood' AND u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Kolkata Riverbank Flood Alert');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Guwahati Flash Flood - Beltola', 'Localized flash flooding disrupted schools and public distribution routes.', dt.id, 'HIGH', 'ACTIVE', 26.1445, 91.7362, 'Beltola, Guwahati', 8.9, 1150, u.id, DATE_SUB(@now, INTERVAL 10 DAY), NULL, DATE_SUB(@now, INTERVAL 10 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Flood' AND u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Guwahati Flash Flood - Beltola');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Dibrugarh Embankment Breach Response', 'Embankment breach impacted villages requiring rescue boats and dry rations.', dt.id, 'CRITICAL', 'ACTIVE', 27.4728, 94.9120, 'Dibrugarh, Assam', 21.0, 2400, u.id, DATE_SUB(@now, INTERVAL 5 DAY), NULL, DATE_SUB(@now, INTERVAL 5 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Flood' AND u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Dibrugarh Embankment Breach Response');

INSERT INTO disasters (title, description, disaster_type_id, severity, status, latitude, longitude, location_name, affected_area_km, affected_people, reported_by, start_date, end_date, created_at)
SELECT 'Bhubaneswar Earth Tremor Preparedness', 'Minor tremors triggered precautionary evacuation and emergency inspections.', dt.id, 'LOW', 'CLOSED', 20.2961, 85.8245, 'Bhubaneswar City', 3.2, 140, u.id, DATE_SUB(@now, INTERVAL 57 DAY), DATE_SUB(@now, INTERVAL 55 DAY), DATE_SUB(@now, INTERVAL 57 DAY)
FROM disaster_types dt, users u
WHERE dt.name = 'Earthquake' AND u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM disasters WHERE title = 'Bhubaneswar Earth Tremor Preparedness');

-- =============================================
-- VOLUNTEERS
-- =============================================
INSERT INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, emergency_contact, is_verified, total_hours, rating, created_at)
SELECT u.id, 'First Aid, Evacuation Support, Camp Operations', 'Hindi, English, Marathi', 4, 'AVAILABLE', 19.0712, 72.8826, 'Chembur, Mumbai', '9876501201', TRUE, 128, 4.7, DATE_SUB(@now, INTERVAL 45 DAY)
FROM users u WHERE u.username = 'volunteer_mum_01'
  AND NOT EXISTS (SELECT 1 FROM volunteers v WHERE v.user_id = u.id);

INSERT INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, emergency_contact, is_verified, total_hours, rating, created_at)
SELECT u.id, 'Logistics, Food Distribution, Crowd Control', 'Hindi, English', 3, 'BUSY', 19.2183, 72.9781, 'Thane West', '9876501202', TRUE, 96, 4.5, DATE_SUB(@now, INTERVAL 44 DAY)
FROM users u WHERE u.username = 'volunteer_mum_02'
  AND NOT EXISTS (SELECT 1 FROM volunteers v WHERE v.user_id = u.id);

INSERT INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, emergency_contact, is_verified, total_hours, rating, created_at)
SELECT u.id, 'Nursing Support, Shelter Triage, Child Care', 'Tamil, English', 5, 'AVAILABLE', 12.9887, 80.2350, 'Perungudi, Chennai', '9876501203', TRUE, 152, 4.8, DATE_SUB(@now, INTERVAL 44 DAY)
FROM users u WHERE u.username = 'volunteer_che_01'
  AND NOT EXISTS (SELECT 1 FROM volunteers v WHERE v.user_id = u.id);

INSERT INTO volunteers (user_id, skills, languages, experience_years, availability, latitude, longitude, address, emergency_contact, is_verified, total_hours, rating, created_at)
SELECT u.id, 'Boat Rescue, Water Safety, Local Coordination', 'Assamese, Hindi, English', 6, 'AVAILABLE', 26.1615, 91.7671, 'Dispur, Guwahati', '9876501204', TRUE, 181, 4.9, DATE_SUB(@now, INTERVAL 43 DAY)
FROM users u WHERE u.username = 'volunteer_asm_01'
  AND NOT EXISTS (SELECT 1 FROM volunteers v WHERE v.user_id = u.id);

-- =============================================
-- VICTIMS + RELIEF REQUESTS
-- =============================================
INSERT INTO victims (full_name, phone, address, latitude, longitude, family_size, special_needs, status, disaster_id, created_at)
SELECT 'Shabana Sheikh', '9111110001', 'Nehru Nagar, Kurla', 19.0657, 72.8784, 6, 'Diabetic elder', 'RECEIVING_AID', d.id, DATE_SUB(@now, INTERVAL 23 DAY)
FROM disasters d
WHERE d.title = 'Mumbai Monsoon Flooding - Kurla Cluster'
  AND NOT EXISTS (SELECT 1 FROM victims v WHERE v.full_name = 'Shabana Sheikh' AND v.disaster_id = d.id);

INSERT INTO victims (full_name, phone, address, latitude, longitude, family_size, special_needs, status, disaster_id, created_at)
SELECT 'Karthik Elangovan', '9111110002', 'Foreshore Estate Block C', 13.0407, 80.2852, 4, 'Child with asthma', 'SHELTERED', d.id, DATE_SUB(@now, INTERVAL 17 DAY)
FROM disasters d
WHERE d.title = 'Chennai Cyclone Surge - Marina Belt'
  AND NOT EXISTS (SELECT 1 FROM victims v WHERE v.full_name = 'Karthik Elangovan' AND v.disaster_id = d.id);

INSERT INTO victims (full_name, phone, address, latitude, longitude, family_size, special_needs, status, disaster_id, created_at)
SELECT 'Rahima Begum', '9111110003', 'Beltola Tiniali', 26.1260, 91.8031, 5, 'Pregnant woman', 'REGISTERED', d.id, DATE_SUB(@now, INTERVAL 9 DAY)
FROM disasters d
WHERE d.title = 'Guwahati Flash Flood - Beltola'
  AND NOT EXISTS (SELECT 1 FROM victims v WHERE v.full_name = 'Rahima Begum' AND v.disaster_id = d.id);

INSERT INTO victims (full_name, phone, address, latitude, longitude, family_size, special_needs, status, disaster_id, created_at)
SELECT 'Prasenjit Bora', '9111110004', 'Lahowal Village, Dibrugarh', 27.4702, 94.9013, 7, 'Wheelchair support', 'REGISTERED', d.id, DATE_SUB(@now, INTERVAL 4 DAY)
FROM disasters d
WHERE d.title = 'Dibrugarh Embankment Breach Response'
  AND NOT EXISTS (SELECT 1 FROM victims v WHERE v.full_name = 'Prasenjit Bora' AND v.disaster_id = d.id);

INSERT INTO relief_requests (victim_id, disaster_id, request_type, description, urgency_level, quantity_needed, status, notes, created_at)
SELECT v.id, d.id, 'WATER', 'Need safe drinking water cans for 6 family members.', 5, 18, 'IN_PROGRESS', 'Escalated due to contamination risk.', DATE_SUB(@now, INTERVAL 22 DAY)
FROM victims v JOIN disasters d ON d.id = v.disaster_id
WHERE v.full_name = 'Shabana Sheikh' AND d.title = 'Mumbai Monsoon Flooding - Kurla Cluster'
  AND NOT EXISTS (SELECT 1 FROM relief_requests r WHERE r.victim_id = v.id AND r.request_type = 'WATER');

INSERT INTO relief_requests (victim_id, disaster_id, request_type, description, urgency_level, quantity_needed, status, notes, created_at)
SELECT v.id, d.id, 'MEDICAL', 'Immediate inhaler and pediatric check-up required.', 5, 1, 'ASSIGNED', 'Volunteer nurse assigned.', DATE_SUB(@now, INTERVAL 16 DAY)
FROM victims v JOIN disasters d ON d.id = v.disaster_id
WHERE v.full_name = 'Karthik Elangovan' AND d.title = 'Chennai Cyclone Surge - Marina Belt'
  AND NOT EXISTS (SELECT 1 FROM relief_requests r WHERE r.victim_id = v.id AND r.request_type = 'MEDICAL');

INSERT INTO relief_requests (victim_id, disaster_id, request_type, description, urgency_level, quantity_needed, status, notes, created_at)
SELECT v.id, d.id, 'SHELTER', 'Temporary shelter for family of five.', 4, 1, 'PENDING', 'Awaiting nearby shelter capacity confirmation.', DATE_SUB(@now, INTERVAL 9 DAY)
FROM victims v JOIN disasters d ON d.id = v.disaster_id
WHERE v.full_name = 'Rahima Begum' AND d.title = 'Guwahati Flash Flood - Beltola'
  AND NOT EXISTS (SELECT 1 FROM relief_requests r WHERE r.victim_id = v.id AND r.request_type = 'SHELTER');

INSERT INTO relief_requests (victim_id, disaster_id, request_type, description, urgency_level, quantity_needed, status, notes, created_at)
SELECT v.id, d.id, 'RESCUE', 'Boat evacuation needed for elderly and children.', 5, 1, 'PENDING', 'Road access blocked by high water.', DATE_SUB(@now, INTERVAL 4 DAY)
FROM victims v JOIN disasters d ON d.id = v.disaster_id
WHERE v.full_name = 'Prasenjit Bora' AND d.title = 'Dibrugarh Embankment Breach Response'
  AND NOT EXISTS (SELECT 1 FROM relief_requests r WHERE r.victim_id = v.id AND r.request_type = 'RESCUE');

-- =============================================
-- ASSIGNMENTS (team activity)
-- =============================================
INSERT INTO assignments (volunteer_id, disaster_id, relief_request_id, assigned_by, status, notes, assigned_at, accepted_at, completed_at, hours_logged)
SELECT vol.id, d.id, rr.id, ua.id, 'IN_PROGRESS', 'Water distribution and sanitation awareness.', DATE_SUB(@now, INTERVAL 21 DAY), DATE_SUB(@now, INTERVAL 21 DAY), NULL, 14.5
FROM volunteers vol
JOIN users uv ON uv.id = vol.user_id
JOIN disasters d ON d.title = 'Mumbai Monsoon Flooding - Kurla Cluster'
JOIN relief_requests rr ON rr.disaster_id = d.id AND rr.request_type = 'WATER'
JOIN users ua ON ua.username = 'ops_lead_mumbai'
WHERE uv.username = 'volunteer_mum_01'
  AND NOT EXISTS (SELECT 1 FROM assignments a WHERE a.volunteer_id = vol.id AND a.relief_request_id = rr.id);

INSERT INTO assignments (volunteer_id, disaster_id, relief_request_id, assigned_by, status, notes, assigned_at, accepted_at, completed_at, hours_logged)
SELECT vol.id, d.id, rr.id, ua.id, 'COMPLETED', 'Medical check-up completed; meds distributed.', DATE_SUB(@now, INTERVAL 16 DAY), DATE_SUB(@now, INTERVAL 16 DAY), DATE_SUB(@now, INTERVAL 14 DAY), 11.0
FROM volunteers vol
JOIN users uv ON uv.id = vol.user_id
JOIN disasters d ON d.title = 'Chennai Cyclone Surge - Marina Belt'
JOIN relief_requests rr ON rr.disaster_id = d.id AND rr.request_type = 'MEDICAL'
JOIN users ua ON ua.username = 'ops_lead_chennai'
WHERE uv.username = 'volunteer_che_01'
  AND NOT EXISTS (SELECT 1 FROM assignments a WHERE a.volunteer_id = vol.id AND a.relief_request_id = rr.id);

INSERT INTO assignments (volunteer_id, disaster_id, relief_request_id, assigned_by, status, notes, assigned_at, accepted_at, completed_at, hours_logged)
SELECT vol.id, d.id, rr.id, ua.id, 'ASSIGNED', 'Rapid rescue support with local boats.', DATE_SUB(@now, INTERVAL 3 DAY), NULL, NULL, NULL
FROM volunteers vol
JOIN users uv ON uv.id = vol.user_id
JOIN disasters d ON d.title = 'Dibrugarh Embankment Breach Response'
JOIN relief_requests rr ON rr.disaster_id = d.id AND rr.request_type = 'RESCUE'
JOIN users ua ON ua.username = 'admin'
WHERE uv.username = 'volunteer_asm_01'
  AND NOT EXISTS (SELECT 1 FROM assignments a WHERE a.volunteer_id = vol.id AND a.relief_request_id = rr.id);

-- =============================================
-- INVENTORY (includes shortages)
-- =============================================
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

-- =============================================
-- PAYMENTS + DONATIONS (if donation module used)
-- =============================================
INSERT INTO payments (user_id, amount, currency, status, provider, payment_order_id, payment_id, donor_name, donor_email, donor_phone, created_at)
SELECT u.id, 150000.00, 'INR', 'CAPTURED', 'RAZORPAY', 'order_demo_2026_001', 'pay_demo_2026_001', 'Suryanet Logistics Pvt Ltd', 'donor.corp01@example.com', '9000000041', DATE_SUB(@now, INTERVAL 15 DAY)
FROM users u WHERE u.username = 'donor_corp_01'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE payment_order_id = 'order_demo_2026_001');

INSERT INTO payments (user_id, amount, currency, status, provider, payment_order_id, payment_id, donor_name, donor_email, donor_phone, created_at)
SELECT u.id, 25000.00, 'INR', 'CAPTURED', 'RAZORPAY', 'order_demo_2026_002', 'pay_demo_2026_002', 'Neha Kulkarni', 'donor.ind01@example.com', '9000000042', DATE_SUB(@now, INTERVAL 8 DAY)
FROM users u WHERE u.username = 'donor_ind_01'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE payment_order_id = 'order_demo_2026_002');

INSERT INTO payments (user_id, amount, currency, status, provider, payment_order_id, payment_id, donor_name, donor_email, donor_phone, failure_reason, created_at)
SELECT u.id, 10000.00, 'INR', 'FAILED', 'RAZORPAY', 'order_demo_2026_003', 'pay_demo_2026_003', 'Neha Kulkarni', 'donor.ind01@example.com', '9000000042', 'UPI timeout during bank confirmation', DATE_SUB(@now, INTERVAL 3 DAY)
FROM users u WHERE u.username = 'donor_ind_01'
  AND NOT EXISTS (SELECT 1 FROM payments WHERE payment_order_id = 'order_demo_2026_003');

INSERT INTO donations (payment_id, disaster_id, amount, donation_type, description, status, donor_name, donor_email, donor_phone, is_anonymous, created_at)
SELECT p.id, d.id, 150000.00, 'MONETARY', 'Corporate relief fund for flood and shelter operations.', 'CONFIRMED', 'Suryanet Logistics Pvt Ltd', 'donor.corp01@example.com', '9000000041', FALSE, DATE_SUB(@now, INTERVAL 15 DAY)
FROM payments p JOIN disasters d ON d.title = 'Mumbai Monsoon Flooding - Kurla Cluster'
WHERE p.payment_order_id = 'order_demo_2026_001'
  AND NOT EXISTS (SELECT 1 FROM donations dn WHERE dn.payment_id = p.id);

INSERT INTO donations (payment_id, disaster_id, amount, donation_type, description, status, donor_name, donor_email, donor_phone, is_anonymous, created_at)
SELECT p.id, d.id, 25000.00, 'MONETARY', 'Emergency medical assistance contribution.', 'CONFIRMED', 'Neha Kulkarni', 'donor.ind01@example.com', '9000000042', FALSE, DATE_SUB(@now, INTERVAL 8 DAY)
FROM payments p JOIN disasters d ON d.title = 'Chennai Cyclone Surge - Marina Belt'
WHERE p.payment_order_id = 'order_demo_2026_002'
  AND NOT EXISTS (SELECT 1 FROM donations dn WHERE dn.payment_id = p.id);

INSERT INTO payment_events (payment_id, event_type, payload, processed, created_at)
SELECT p.id, 'payment.captured', JSON_OBJECT('source', 'seed_demo', 'note', 'captured demo event'), TRUE, DATE_SUB(@now, INTERVAL 15 DAY)
FROM payments p
WHERE p.payment_order_id = 'order_demo_2026_001'
  AND NOT EXISTS (SELECT 1 FROM payment_events pe WHERE pe.payment_id = p.id AND pe.event_type = 'payment.captured');

INSERT INTO payment_events (payment_id, event_type, payload, processed, created_at)
SELECT p.id, 'payment.failed', JSON_OBJECT('source', 'seed_demo', 'note', 'failed demo event'), TRUE, DATE_SUB(@now, INTERVAL 3 DAY)
FROM payments p
WHERE p.payment_order_id = 'order_demo_2026_003'
  AND NOT EXISTS (SELECT 1 FROM payment_events pe WHERE pe.payment_id = p.id AND pe.event_type = 'payment.failed');

-- =============================================
-- NOTIFICATIONS
-- =============================================
INSERT INTO notifications (user_id, title, message, notification_type, reference_type, reference_id, is_read, created_at)
SELECT u.id, 'High Priority Flood Escalation', 'Kurla flood zone moved to high-priority response. Report every 2 hours.', 'DISASTER_ALERT', 'DISASTER', d.id, FALSE, DATE_SUB(@now, INTERVAL 23 DAY)
FROM users u JOIN disasters d ON d.title = 'Mumbai Monsoon Flooding - Kurla Cluster'
WHERE u.username = 'ops_lead_mumbai'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'High Priority Flood Escalation' AND n.reference_id = d.id);

INSERT INTO notifications (user_id, title, message, notification_type, reference_type, reference_id, is_read, created_at)
SELECT u.id, 'Medical Request Assigned', 'You have been assigned an urgent pediatric medical request at Marina shelter.', 'ASSIGNMENT', 'RELIEF_REQUEST', rr.id, TRUE, DATE_SUB(@now, INTERVAL 16 DAY)
FROM users u
JOIN volunteers v ON v.user_id = u.id
JOIN disasters d ON d.title = 'Chennai Cyclone Surge - Marina Belt'
JOIN relief_requests rr ON rr.disaster_id = d.id AND rr.request_type = 'MEDICAL'
WHERE u.username = 'volunteer_che_01'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'Medical Request Assigned' AND n.reference_id = rr.id);

INSERT INTO notifications (user_id, title, message, notification_type, reference_type, reference_id, is_read, created_at)
SELECT u.id, 'Inventory Shortage Alert', 'Drinking water stock is below threshold at Mumbai Relief Camp - Sion.', 'INVENTORY_ALERT', 'INVENTORY', i.id, FALSE, DATE_SUB(@now, INTERVAL 2 DAY)
FROM users u
JOIN locations l ON l.name = 'Mumbai Relief Camp - Sion'
JOIN inventory i ON i.location_id = l.id AND i.item_name = 'Drinking Water 20L Can'
WHERE u.username = 'ops_lead_mumbai'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'Inventory Shortage Alert' AND n.reference_id = i.id);

INSERT INTO notifications (user_id, title, message, notification_type, reference_type, reference_id, is_read, created_at)
SELECT u.id, 'Donation Received', 'A confirmed donation of INR 150000 has been received for Mumbai flood response.', 'DONATION', 'DONATION', dn.id, FALSE, DATE_SUB(@now, INTERVAL 15 DAY)
FROM users u
JOIN donations dn ON dn.donor_email = 'donor.corp01@example.com'
WHERE u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM notifications n WHERE n.user_id = u.id AND n.title = 'Donation Received' AND n.reference_id = dn.id);

-- =============================================
-- NEWS FEED
-- =============================================
INSERT INTO news_updates (title, summary, content, image_url, disaster_type, severity, status, location, latitude, longitude, source_incident_id, created_by, created_at, updated_at)
SELECT
  'Kurla Flooding Intensifies: Evacuation Extended Overnight',
  'Rescue corridors were expanded in Kurla and Sion as flood levels remained high through the night.',
  'Command teams confirmed additional boats and medical units were dispatched to low-lying pockets. Shelters at Sion and Dharavi have been expanded with food and water support. Authorities continue to monitor storm drains and transport disruptions.',
  'https://images.unsplash.com/photo-1454789548928-9efd52dc4031?auto=format&fit=crop&w=1400&q=80',
  'Flood',
  'CRITICAL',
  'ACTIVE',
  'Kurla, Mumbai',
  19.0735,
  72.8790,
  d.id,
  u.id,
  DATE_SUB(@now, INTERVAL 23 DAY),
  DATE_SUB(@now, INTERVAL 2 HOUR)
FROM disasters d, users u
WHERE d.title = 'Mumbai Monsoon Flooding - Kurla Cluster' AND u.username = 'ops_lead_mumbai'
  AND NOT EXISTS (SELECT 1 FROM news_updates n WHERE n.title = 'Kurla Flooding Intensifies: Evacuation Extended Overnight');

INSERT INTO news_updates (title, summary, content, image_url, disaster_type, severity, status, location, latitude, longitude, source_incident_id, created_by, created_at, updated_at)
SELECT
  'Chennai Cyclone Surge: Coastal Shelters Stable',
  'Shelter occupancy remains controlled as coastal teams continue monitoring wind and rainfall spikes.',
  'The operations desk reports stable occupancy across Velachery and nearby shelters. Medical response teams are on standby while utility crews restore localized outages. Public advisories remain active for vulnerable wards.',
  'https://images.unsplash.com/photo-1527489377706-5bf97e608852?auto=format&fit=crop&w=1400&q=80',
  'Cyclone',
  'HIGH',
  'MONITORING',
  'Marina Belt, Chennai',
  13.0352,
  80.2824,
  d.id,
  u.id,
  DATE_SUB(@now, INTERVAL 13 DAY),
  DATE_SUB(@now, INTERVAL 6 HOUR)
FROM disasters d, users u
WHERE d.title = 'Chennai Cyclone Surge - Marina Belt' AND u.username = 'ops_lead_chennai'
  AND NOT EXISTS (SELECT 1 FROM news_updates n WHERE n.title = 'Chennai Cyclone Surge: Coastal Shelters Stable');

INSERT INTO news_updates (title, summary, content, image_url, disaster_type, severity, status, location, latitude, longitude, source_incident_id, created_by, created_at, updated_at)
SELECT
  'Navi Mumbai Heatwave Response Closed',
  'Heatwave response operations have been closed after sustained temperature normalization.',
  'District teams confirmed that emergency medical demand has returned to baseline and cooling center operations were formally closed. Post-incident monitoring will continue for the next 72 hours.',
  'https://images.unsplash.com/photo-1500375592092-40eb2168fd21?auto=format&fit=crop&w=1400&q=80',
  'Heatwave',
  'MEDIUM',
  'RESOLVED',
  'Nerul, Navi Mumbai',
  19.0330,
  73.0297,
  d.id,
  u.id,
  DATE_SUB(@now, INTERVAL 33 DAY),
  DATE_SUB(@now, INTERVAL 12 DAY)
FROM disasters d, users u
WHERE d.title = 'Navi Mumbai Heatwave Impact' AND u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM news_updates n WHERE n.title = 'Navi Mumbai Heatwave Response Closed');

INSERT INTO news_timeline_updates (news_id, update_text, update_timestamp)
SELECT n.id, 'First public advisory issued to nearby wards.', DATE_SUB(@now, INTERVAL 22 DAY)
FROM news_updates n
WHERE n.title = 'Kurla Flooding Intensifies: Evacuation Extended Overnight'
  AND NOT EXISTS (SELECT 1 FROM news_timeline_updates t WHERE t.news_id = n.id);

INSERT INTO news_timeline_updates (news_id, update_text, update_timestamp)
SELECT n.id, 'Additional evacuation boats deployed to affected corridors.', DATE_SUB(@now, INTERVAL 18 DAY)
FROM news_updates n
WHERE n.title = 'Kurla Flooding Intensifies: Evacuation Extended Overnight'
  AND NOT EXISTS (SELECT 1 FROM news_timeline_updates t WHERE t.news_id = n.id AND t.update_text LIKE 'Additional evacuation boats%');

INSERT INTO news_timeline_updates (news_id, update_text, update_timestamp)
SELECT n.id, 'Shelter occupancy reviewed; medical readiness maintained.', DATE_SUB(@now, INTERVAL 10 DAY)
FROM news_updates n
WHERE n.title = 'Chennai Cyclone Surge: Coastal Shelters Stable'
  AND NOT EXISTS (SELECT 1 FROM news_timeline_updates t WHERE t.news_id = n.id);

INSERT INTO news_timeline_updates (news_id, update_text, update_timestamp)
SELECT n.id, 'Heatwave response formally closed and transitioned to monitoring.', DATE_SUB(@now, INTERVAL 12 DAY)
FROM news_updates n
WHERE n.title = 'Navi Mumbai Heatwave Response Closed'
  AND NOT EXISTS (SELECT 1 FROM news_timeline_updates t WHERE t.news_id = n.id);

-- =============================================
-- AUDIT LOGS
-- =============================================
INSERT INTO audit_logs (user_id, action, entity_type, entity_id, old_value, new_value, ip_address, user_agent, created_at)
SELECT u.id, 'UPDATE_STATUS', 'DISASTER', d.id,
       JSON_OBJECT('status', 'REPORTED'), JSON_OBJECT('status', 'ACTIVE'),
       '103.86.18.22', 'Mozilla/5.0 DemoOps', DATE_SUB(@now, INTERVAL 24 DAY)
FROM users u JOIN disasters d ON d.title = 'Mumbai Monsoon Flooding - Kurla Cluster'
WHERE u.username = 'ops_lead_mumbai'
  AND NOT EXISTS (SELECT 1 FROM audit_logs a WHERE a.user_id = u.id AND a.entity_type = 'DISASTER' AND a.entity_id = d.id AND a.action = 'UPDATE_STATUS');

INSERT INTO audit_logs (user_id, action, entity_type, entity_id, old_value, new_value, ip_address, user_agent, created_at)
SELECT u.id, 'CREATE', 'ASSIGNMENT', a.id,
       NULL, JSON_OBJECT('status', 'ASSIGNED', 'hours_logged', NULL),
       '49.37.221.90', 'Mozilla/5.0 FieldCoordinator', DATE_SUB(@now, INTERVAL 3 DAY)
FROM users u
JOIN assignments a ON a.notes = 'Rapid rescue support with local boats.'
WHERE u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM audit_logs al WHERE al.user_id = u.id AND al.entity_type = 'ASSIGNMENT' AND al.entity_id = a.id AND al.action = 'CREATE');

COMMIT;
