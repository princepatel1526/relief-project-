-- Normalize roles.name away from MySQL ENUM and standardize role values.
-- Safe to re-run.

ALTER TABLE roles
  MODIFY COLUMN name VARCHAR(80) NOT NULL;

-- Remove legacy roles and related links.
DELETE ur
FROM user_roles ur
JOIN roles r ON r.id = ur.role_id
WHERE r.name IN ('ROLE_COORDINATOR', 'ROLE_DONOR', 'ROLE_NGO');

DELETE FROM roles WHERE name IN ('ROLE_COORDINATOR', 'ROLE_DONOR', 'ROLE_NGO');

-- Ensure final standardized role set exists.
INSERT INTO roles (name, description)
SELECT 'ROLE_CITIZEN', 'Citizen reporting incidents and requesting support'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_CITIZEN');

INSERT INTO roles (name, description)
SELECT 'ROLE_VOLUNTEER', 'Field volunteer providing relief'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_VOLUNTEER');

INSERT INTO roles (name, description)
SELECT 'ROLE_RESPONDER', 'Professional first responder'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_RESPONDER');

INSERT INTO roles (name, description)
SELECT 'ROLE_NGO_COORDINATOR', 'NGO coordinator managing partner operations'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_NGO_COORDINATOR');

INSERT INTO roles (name, description)
SELECT 'ROLE_ADMIN', 'System administrator with operational access'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');

INSERT INTO roles (name, description)
SELECT 'ROLE_SUPER_ADMIN', 'Super administrator with full cross-region access'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_SUPER_ADMIN');
