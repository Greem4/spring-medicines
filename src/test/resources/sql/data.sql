TRUNCATE TABLE medicines RESTART IDENTITY CASCADE;

INSERT INTO medicines (name, serial_number, expiration_date)
VALUES ('тест', 'тест', '2099-06-30');

