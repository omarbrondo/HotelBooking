# Usa una imagen de Maven para compilar el proyecto
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia el backend
COPY hotelbooking-backend/. .

# Copia el frontend
COPY hotelbooking-frontend/static ./src/main/resources/static

# Compila el proyecto y empaqueta el jar
RUN mvn clean package -DskipTests

# Usa una imagen ligera de Java para correr la app
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia el jar generado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto por defecto de Spring Boot
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
