--liquibase formatted sql

--changeset Greem4:1
CREATE TYPE user_role AS ENUM ('ADMIN', 'USER');

--changeset Greem4:2
CREATE TABLE users
(
    id       BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    enabled  BOOLEAN      NOT NULL DEFAULT TRUE,
    role     user_role    NOT NULL
);

--changeset Greem4:3
INSERT INTO users (username, password, enabled, role)
VALUES ('admin', 'admin', TRUE, 'ADMIN'),
       ('user', 'user', TRUE, 'USER');
