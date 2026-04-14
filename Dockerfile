# Use Java 21 (change if your app uses a different version)
FROM eclipse-temurin:21-jdk-jammy

# Set working directory
WORKDIR /app

# Copy jar file
COPY target/*.jar app.jar

# Run the app
ENTRYPOINT ["java","-jar","app.jar"]
