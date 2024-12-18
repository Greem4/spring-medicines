--liquibase formatted sql

--changeset Greem4:1
CREATE TABLE medicines (
                           id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                           name VARCHAR(255) NOT NULL,
                           serial_number VARCHAR(255) NOT NULL,
                           expiration_date DATE NOT NULL
);

--changeset Greem4:2
ALTER TABLE medicines
    ADD COLUMN created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;