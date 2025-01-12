# ------------------------------------
# Этап сборки (build stage)
# ------------------------------------
FROM gradle:8.5-jdk21 AS build
LABEL authors="greem4"

WORKDIR /app

COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
COPY gradle ./gradle

RUN gradle dependencies || true

COPY src ./src

RUN gradle clean bootJar -x test

# ------------------------------------
# Этап выполнения (runtime stage)
# ------------------------------------
FROM openjdk:21-jdk

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]