# Multi-stage build for Spring Boot application
FROM maven:3.9.4-openjdk-17-slim AS builder

WORKDIR /app

# Copy pom.xml and download dependencies first for better caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# Create a non-root user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy the built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership of the app directory
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port (Railway will provide PORT env variable)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-Dserver.port=${PORT:-8080}", "-Xmx512m", "-jar", "app.jar"]