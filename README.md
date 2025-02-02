# Spring Medicines

**Spring Medicines** – пет проект на **Spring Boot** для управления списком лекарственных препаратов с поддержкой аутентификации/авторизации (через локальный логин/пароль и OAuth2 Яндекс), а также с отправкой email-уведомлений об истечении срока годности.

## Стек технологий

- **Язык**: Java 21+
- **Фреймворк**: Spring Boot 3.4.0 (Security, Data JPA, Mail)
- **База данных**: PostgreSQL
- **Миграции**: Liquibase
- **Аутентификация**:
  - Локальная (логин/пароль, `BCryptPasswordEncoder`)
  - OAuth2 (через Яндекс) с выдачей собственного JWT
- **Авторизация**: ролевой доступ (**USER**, **ADMIN**)
- **Токены**: **JWT**
- **Отправка email**: `JavaMailSender + RabbitMQ` + SMTP
- **Сборка**: Gradle
- **Тестирование**: JUnit, Testcontainers
- **Документация API**: Springdoc OpenAPI

## Функциональность

1. **Просмотр препаратов без авторизации**
  - `GET /api/medicines` (список)
  - `GET /api/medicines/{id}` (детали)

2. **Аутентификация**
  - **Локальная**: `POST /api/auth/login` (выдаёт JWT)
  - **OAuth2 через Яндекс**: при успехе выдает локальный JWT

3. **Авторизация (роли)**
  - **USER** — может смотреть список и детали
  - **ADMIN** — может добавлять (`POST`), обновлять (`PUT`), удалять (`DELETE`) препараты, а также управлять пользователями.

4. **Email-уведомления**
  - При приближении даты истечения срока годности отправляется письмо на email.
  - SMTP-настройки (логин, пароль, хост, порт) задаются в `application.yaml` или через `.env`.
  - Используется `JavaMailSender -> RabbitMQ -> Mail`.

5. **Liquibase**
  - Для миграций БД: автоматически создаёт таблицы и вставляет тестовые данные.

6. **Документация API**
  - Доступна по адресу: `http://localhost:8080/swagger-ui/index.html` (Springdoc OpenAPI).

## Ключевые особенности

- **JWT**: При входе (логин/пароль или OAuth2 Яндекс) приложение генерирует JWT, который клиент далее передаёт в заголовке `Authorization: Bearer ...`.
- **Яндекс OAuth2**: При успешном входе через Яндекс пользователь тоже получает JWT, что делает приложение stateless.
- **Roles**: `hasRole('ADMIN')` для создания/обновления/удаления, `permitAll()` для просмотра.

## Запуск и использование

1. **Склонировать** репозиторий.
2. **Скопировать** `.env.template` в `.env`, заполнить переменные окружения (SMTP, JWT секрет и т.д.).
3. Запустить `./gradlew bootRun` или собрать `./gradlew build` и запуск `java -jar build/libs/spring-medicines-<version>.jar`.
4. **Swagger**: `GET http://localhost:8080/swagger-ui/index.html`.

## Тестирование

- **Интеграционные тесты**: Spring Boot Test + Testcontainers (поднимают PostgreSQL контейнер)
- Запуск: `./gradlew test`
