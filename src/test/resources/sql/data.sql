TRUNCATE TABLE medicine RESTART IDENTITY CASCADE;
INSERT INTO medicine (name, serial_number, expiration_date)
VALUES ('тест', 'тест', '2099-06-30');

