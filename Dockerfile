# Stage 1: Build
FROM gradle:8-jdk17 AS build
WORKDIR /app
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies
COPY src ./src
RUN ./gradlew build -x test

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*-all.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
USER 1000
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
