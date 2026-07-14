# Usa una imagen base con Java 21
FROM openjdk:21-jdk-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo Maven wrapper y el pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Descarga las dependencias (capa cacheable)
RUN ./mvnw dependency:go-offline -B

# Copia el resto del código fuente
COPY src src

# Construye la aplicación
RUN ./mvnw clean package -DskipTests

# Expone el puerto que usa Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "target/abarrotes-api-1.0.0.jar"]