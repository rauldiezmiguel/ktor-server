# Usa una imagen base con JDK 17
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo tfg-server-all.jar generado por shadowJar al contenedor
COPY build/libs/tfg-server-all.jar /app/tfg-server-all.jar

# Expone el puerto 8080 en el que se ejecutará la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación cuando se inicie el contenedor
CMD ["java", "-jar", "/app/tfg-server-all.jar"]
