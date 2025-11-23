# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .

# Grant execution rights to the gradlew script
RUN chmod +x gradlew

# Download dependencies (this step caches dependencies)
RUN ./gradlew dependencies --no-daemon

# Copy the source code
COPY src/ src/

# Build the fat jar
RUN ./gradlew buildFatJar --no-daemon

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the fat jar from the build stage
COPY --from=build /app/build/libs/quizbackend-all.jar /app/quizbackend-all.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "quizbackend-all.jar"]
