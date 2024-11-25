    # FROM maven:3-openjdk-17 AS build
    # COPY . .
    # RUN  mvn clean package -DskipTests

    # FROM openjdk:17-jdk-slim
    # COPY --from=build /target/Careem-System-0.0.1-SNAPSHOT.jar Careem-System.jar
    # EXPOSE 8080
    # ENTRYPOINT ["java","-jar","Careem-System.jar"]

FROM openjdk:21-jdk-slim
# FROM maven:3.8.4-openjdk-17-slim AS build

ENV JAVA_HOME=/usr/local/openjdk-17

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from the host into the container
COPY target/RideShare-0.0.1-SNAPSHOT.jar /app/RideShare.jar

# Expose the port your application will run on
EXPOSE 3012

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "/app/RideShare.jar", "mvn spring-boot:run"]