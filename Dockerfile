# Use Maven to build the project
FROM maven:3.6.3-openjdk-17-slim AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Use the official OpenJDK 17 image as a base image
FROM openjdk:17-ea-18-jdk-slim
COPY --from=build /app/target/MonarchUniversity-0.0.1-SNAPSHOT.jar /MonarchUniversity-0.0.1-SNAPSHOT.jar

EXPOSE 8080

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "MonarchUniversity-0.0.1-SNAPSHOT.jar"]