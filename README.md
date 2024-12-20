# Spring Medicines

Приложение для управления лекарственными препаратами с использованием Spring Boot, Spring Data JPA, Spring Security и PostgreSQL.  
Позволяет просматривать список препаратов без авторизации, а также добавлять, обновлять и удалять препараты при наличии роли `ADMIN`.
ер и срок годности. Приложение использует Spring Boot для серверной части и Thymeleaf для отображения данных на фронтенде.

## Стек технологий

- **Backend**: Java 23+, Spring Boot 3.4.0, Spring Data JPA, Spring Security
- **Database**: PostgreSQL
- **Migrations**: Liquibase
- **Build Tool**: Gradle
- **Testing**: JUnit, Testcontainers

## Функциональность

- **Без авторизации**: Просмотр списка и деталей препаратов (GET запросы).
- **Под пользователем с ролью `ADMIN`**: Добавление (POST), обновление (PUT) и удаление (DELETE) препаратов.
- **Аутентификация**: HTTP Basic (можно упростить для тестирования, использовать NoOpPasswordEncoder).
- **Авторизация**: Ролевой доступ на уровне эндпоинтов.

## Мини pet проект для себя.

