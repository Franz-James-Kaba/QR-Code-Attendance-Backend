# Use an official Maven image with Java 21 for building the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy source code and build the application
COPY . .
RUN mvn clean package -DskipTests

# Use a lightweight JDK 21 image for running the app
FROM eclipse-temurin:21-jdk-alpine

# Create a non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8090

# Set environment variables (override in docker-compose.yml)
ENV JAVA_OPTS=""

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

