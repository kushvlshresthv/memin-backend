# Use Eclipse Temurin JDK 22 as base image for building
FROM eclipse-temurin:22-jdk-alpine AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application (skip tests for faster builds)
RUN ./mvnw clean package -DskipTests

# Use Eclipse Temurin JRE 22 for runtime (smaller image)
FROM eclipse-temurin:22-jre-alpine

# Set working directory
WORKDIR /app

# Copy the JAR file from build stage
COPY --from=build /app/target/mmms-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port (Render will use the PORT environment variable)
EXPOSE 8080

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
# Render sets PORT env variable, so we bind to it
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]
