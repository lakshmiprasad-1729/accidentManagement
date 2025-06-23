FROM eclipse-temurin:21-jdk

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy everything
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Find and rename the JAR file for consistency
RUN mv target/*.jar target/app.jar

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-Dserver.port=${PORT:-8080}", "-Xmx512m", "-jar", "target/app.jar"]