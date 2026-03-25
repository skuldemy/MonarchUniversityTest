# Use Maven to build the project
FROM maven:3.6.3-openjdk-17-slim AS build

# Copy source code
COPY . /app
WORKDIR /app

# Build jar
RUN mvn clean package -DskipTests

# Use OpenJDK 17 for runtime
FROM openjdk:17-ea-18-jdk-slim

# Copy the jar from build stage
COPY --from=build /app/target/MonarchUniversity-0.0.1-SNAPSHOT.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
