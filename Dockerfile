FROM eclipse-temurin:21-jdk

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy everything
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-Dserver.port=${PORT:-8080}", "-Xmx512m", "-jar", "target/accidentManagement-0.0.1-SNAPSHOT.jar"]