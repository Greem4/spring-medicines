FROM gradle:8.5-jdk21-alpine AS build
WORKDIR /app

COPY gradle.* gradlew ./
COPY gradle ./gradle
RUN ./gradlew --no-daemon --quiet dependencies

COPY src ./src
COPY build.gradle settings.gradle ./
RUN ./gradlew --no-daemon --quiet clean bootJar -x test

FROM eclipse-temurin:21-jre-alpine as jre
RUN $JAVA_HOME/bin/jlink \
    --add-modules java.base,java.logging,java.management,java.nio,java.sql \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /jre-minimal

FROM alpine:3.19
COPY --from=jre /jre-minimal /opt/jre
COPY --from=build /app/build/libs/*.jar /app/app.jar

RUN apk add --no-cache tzdata && \
    ln -sf /usr/share/zoneinfo/Europe/Moscow /etc/localtime

ENV JAVA_HOME=/opt/jre
ENV PATH="$JAVA_HOME/bin:$PATH"
ENV JAVA_OPTS="-XX:MaxRAM=128M -XX:MaxRAMPercentage=70 -XX:+UseSerialGC -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -Xss512k -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]