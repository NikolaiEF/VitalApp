# Build stage
FROM gradle:latest AS build
WORKDIR /home/gradle/src

# Copiar código fuente (no necesitas instalar JDK manualmente, la imagen ya lo incluye)
COPY --chown=gradle:gradle . .

# Construir la aplicación (combinamos los comandos para reducir capas)
RUN gradle clean bootJar

# Fase de ejecución (Package stage)
FROM openjdk:17-jdk-slim
WORKDIR /app

# Variable para el archivo JAR (ajustado para capturar el nombre correcto)
ARG JAR_FILE=VitalApp-*.jar

# Copiar el JAR generado desde la fase de construcción
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Puerto expuesto (definido por Render)
EXPOSE ${PORT}

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]


WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
