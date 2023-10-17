# Set base image
FROM openjdk:17-jdk-alpine

# Set working directory
WORKDIR /app
#copy . /app                                         #it takes too much space
Copy target/spring-boot-docker.jar app.jar

# Expose port 8080
EXPOSE 8080

# Set command to run the application
CMD ["java", "-jar", "app.jar"]
