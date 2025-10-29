FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
COPY target/clientservice-1.0.jar app.jar

EXPOSE 8081
ENV SERVER_PORT=8081
USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]
