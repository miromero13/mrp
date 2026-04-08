# Fase 1: Compilación del proyecto
FROM gradle:jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

# Fase 2: Ejecución del proyecto
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Expone el puerto que usará la aplicación
EXPOSE 8090

# Comando para ejecutar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]