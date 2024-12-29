--liquibase formatted sql


--changeset Greem4:1
BEGIN;

DELETE FROM users
WHERE username IN ('admin', 'user');

INSERT INTO users (username, password, enabled, role)
VALUES
    ('admin', '$2a$10$jQW41fRoFpFRj3lABONpqOwnBXsOazglWGP.iF0gSsPch.SPhBSq2', TRUE, 'ADMIN'),
    ('user', '$2a$10$oOCO7kSJxg0rKU7.zjuEUuPV0usbRogkad3hzVY23J8t8oRecs77u', TRUE, 'USER');

COMMIT;

