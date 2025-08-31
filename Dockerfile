# Stage 1: Build
FROM gradle:8-jdk21 AS build

WORKDIR /app

# Copy Gradle wrapper and build files, including gradle.properties
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle gradle.properties ./

# Make gradlew executable
RUN chmod +x gradlew

# (Optional) Pre-fetch dependencies to speed up rebuilds
RUN ./gradlew dependencies --no-daemon || return 0

# Copy source code
COPY src ./src

# Build Quarkus runner JAR (skip tests for faster build)
RUN ./gradlew quarkusBuild -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy Quarkus build output
COPY --from=build /app/build/quarkus-app/lib/ ./lib/
COPY --from=build /app/build/quarkus-app/app/ ./app/
COPY --from=build /app/build/quarkus-app/quarkus/ ./quarkus/
COPY --from=build /app/build/quarkus-app/quarkus-run.jar ./app.jar

# Expose Quarkus default port
EXPOSE 8080

# Optional JVM options
ENV JAVA_OPTS=""

# Create a non-root user and switch to it
RUN addgroup --system app && adduser --system --ingroup app app
USER app

# Start the app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
