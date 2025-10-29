FROM eclipse-temurin:21-jre-alpine AS runtime

# Create a non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy the JAR from Maven target folder
COPY target/backendservice-1.0.jar app.jar

# Port
EXPOSE 8080

ENV SERVER_PORT=8080

# Use non-root user
USER appuser

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]
