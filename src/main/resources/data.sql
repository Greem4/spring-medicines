DROP TABLE medicines;

CREATE TABLE medicines
(
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(255)        NOT NULL,
    serial_number   VARCHAR(255) UNIQUE NOT NULL,
    expiration_date DATE                NOT NULL
);
ALTER TABLE medicines ALTER COLUMN id TYPE BIGINT;

CREATE TABLE medicines (
                           id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                           name VARCHAR(255) NOT NULL,
                           serial_number VARCHAR(255) NOT NULL,
                           expiration_date DATE NOT NULL
);

INSERT INTO medicines (name, serial_number, expiration_date)
VALUES ('Аспирин', 'A12345', '2024-12-31'),
       ('Ибупрофен', 'B67890', '2025-06-15');

