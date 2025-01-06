TRUNCATE TABLE medicine RESTART IDENTITY CASCADE;
-- fixme: если ты набиваешь тестовые данные - это логичнее в каком-нибудь @BeforeAll делать.
--  Иначе размазывается ответственность за инициализацию БД между тест-сорсами и тест-ресурсами
INSERT INTO medicine (name, serial_number, expiration_date)
VALUES ('тест', 'тест', '2099-06-30');

