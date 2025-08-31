# Stage 1: Build
FROM gradle:8-jdk21 AS build

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files, including gradle.properties
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle gradle.properties ./

# Make gradlew executable
RUN chmod +x gradlew

# (Optional) Pre-fetch dependencies to speed up rebuilds
RUN ./gradlew build --dry-run

# Copy source code
COPY src ./src

# Build fat JAR (skip tests for faster build)
RUN ./gradlew build -x test

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy fat JAR from build stage
COPY --from=build /app/build/libs/*-all.jar app.jar

# Expose Quarkus default port
EXPOSE 8080

# Optional JVM options
ENV JAVA_OPTS=""

# Create a non-root user and switch to it
RUN addgroup --system app && adduser --system --ingroup app app
USER app

# Start the app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
