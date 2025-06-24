FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy pom.xml and source
COPY pom.xml .
COPY src ./src

# Install Maven and build
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*
RUN mvn clean package -DskipTests

# Debug: List the contents of target directory
RUN ls -la target/

# Create a consistent JAR name
RUN cp target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-Dserver.port=${PORT:-8080}", "-Xmx512m", "-jar", "app.jar"]