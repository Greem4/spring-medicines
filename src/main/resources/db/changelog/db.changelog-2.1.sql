--liquibase formatted sql

--changeset Greem4:1
INSERT INTO users (username, password, enabled, role)
VALUES
       ('admin2', 'admin2', TRUE, 'ADMIN')
