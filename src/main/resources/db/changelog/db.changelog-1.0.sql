--liquibase formatted sql

--changeset Greem4:1
CREATE TYPE user_role AS ENUM ('ADMIN', 'USER');

--changeset Greem4:2
CREATE TABLE medicine
(
    id                 BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name               VARCHAR(255) NOT NULL,
    serial_number      VARCHAR(255) NOT NULL,
    expiration_date    DATE         NOT NULL,
    created_date       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset Greem4:3
CREATE TABLE "users"
(
    id                 BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username           VARCHAR(50)  NOT NULL UNIQUE,
    password           VARCHAR(200) NOT NULL,
    enabled            BOOLEAN      NOT NULL DEFAULT TRUE,
    role               user_role    NOT NULL,
    provider           VARCHAR(50),
    provider_id        VARCHAR(100),
    created_date       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset Greem4:4
INSERT INTO "users" (username, password, enabled, role, provider, provider_id)
VALUES ('admin', '$2a$10$jQW41fRoFpFRj3lABONpqOwnBXsOazglWGP.iF0gSsPch.SPhBSq2',
        TRUE, 'ADMIN', 'local', NULL),
       ('user', '$2a$10$oOCO7kSJxg0rKU7.zjuEUuPV0usbRogkad3hzVY23J8t8oRecs77u',
        TRUE, 'USER', 'local', NULL)
