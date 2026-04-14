# Use Java 21 (change if your app uses a different version)
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy jar file
COPY target/*.jar app.jar

# Run the app
ENTRYPOINT ["java","-jar","app.jar"]
