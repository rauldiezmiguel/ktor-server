# Etapa 1: construir la app con Gradle
FROM gradle:8.1.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew shadowJar

# Etapa 2: imagen de producción más liviana
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/tfg-server-all.jar /app/tfg-server-all.jar
EXPOSE 8080
CMD ["java", "-jar", "tfg-server-all.jar"]