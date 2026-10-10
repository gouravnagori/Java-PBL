# ==============================================================================
# Multi-stage Dockerfile for LexAdvisor Legal Document Analyser
# Optimized for Render Cloud Web Service Deployment (Java 21 / Spring Boot 3)
# ==============================================================================

# Stage 1: Build the Spring Boot application using Maven & Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and build production package
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime environment with lightweight Java 21 JRE
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Install fontconfig and basic fonts for Apache PDFBox text extraction
RUN apt-get update && apt-get install -y --no-install-recommends \
    fontconfig \
    libfreetype6 \
    && rm -rf /var/lib/apt/lists/*

# Copy the built jar from build stage
COPY --from=build /app/target/legal-document-analyser-0.0.1-SNAPSHOT.jar app.jar

# Create directory for runtime document uploads
RUN mkdir -p uploads/documents && chmod -R 777 uploads

# Render provides the port dynamically via $PORT
ENV PORT=8080
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
