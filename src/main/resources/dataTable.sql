DROP TABLE medicines;

CREATE TABLE medicines (
                           id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                           name VARCHAR(255) NOT NULL,
                           serial_number VARCHAR(255) NOT NULL,
                           expiration_date DATE NOT NULL
);

