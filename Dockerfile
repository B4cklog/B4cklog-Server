# Build stage
FROM gradle:8.6-jdk21 AS build
WORKDIR /app
COPY .env .env
COPY . .
RUN gradle build --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create non-root user
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy built JAR file
COPY --from=build /app/build/libs/*.jar app.jar
COPY .env .env

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_DEVTOOLS_RESTART_ENABLED=true
ENV SPRING_DEVTOOLS_ADD_PROPERTIES=true
ENV SPRING_DEVTOOLS_RESTART_TRIGGER_FILE=.reloadtrigger

# Expose port
EXPOSE 8080

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 