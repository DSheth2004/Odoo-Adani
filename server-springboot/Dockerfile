# ------------------------------------------------------------
# Multi‑stage build for the Odoo‑Adani Spring‑Boot backend
# ------------------------------------------------------------
# Stage 1 – Build
# Uses Maven + JDK 21 to compile and package the application.
FROM maven:3.9-eclipse-temurin-21 AS build

# Working directory inside the container
WORKDIR /app

# Copy only the Maven descriptor first (allows better caching)
COPY pom.xml .
# Copy the source code
COPY src ./src

# Resolve dependencies and build the JAR (skip tests for speed)
RUN mvn -B -DskipTests clean package

# ------------------------------------------------------------
# Stage 2 – Runtime
# Minimal JRE image to run the compiled JAR.
# ------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

# Working directory for the runtime container
WORKDIR /app

# Pull the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the Spring‑Boot app listens on (default 8080)
EXPOSE 5000

# Entrypoint – runs the JAR. All configuration is pulled from
# environment variables (see `application.properties`).
ENTRYPOINT ["java","-jar","app.jar"]
