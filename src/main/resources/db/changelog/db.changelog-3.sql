--liquibase formatted sql

--changeset Greem4:1
ALTER TABLE users
    ADD COLUMN provider VARCHAR(50),
    ADD COLUMN provider_id VARCHAR(100);

--changeset Greem4:2
UPDATE users
SET provider = 'local',
    provider_id = NULL
WHERE provider IS NULL;

--changeset Greem4:3
BEGIN;

DELETE FROM users
WHERE username IN ('admin', 'user');

INSERT INTO users (username, password, enabled, role, provider, provider_id)
VALUES
    ('admin', '$2a$10$jQW41fRoFpFRj3lABONpqOwnBXsOazglWGP.iF0gSsPch.SPhBSq2', TRUE, 'ADMIN', 'local', NULL),
    ('user', '$2a$10$oOCO7kSJxg0rKU7.zjuEUuPV0usbRogkad3hzVY23J8t8oRecs77u', TRUE, 'USER', 'local', NULL);

COMMIT;

