# Etapa 1: build
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY backend/atendimento/pom.xml .
COPY backend/atendimento/src ./src
RUN mvn clean package -DskipTests

# Etapa 2: runtime
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
