# Usa la imagen oficial de Eclipse Temurin (OpenJDK)
FROM eclipse-temurin:21-jdk-alpine

# Establece el directorio de trabajo
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

# Expone el puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "target/abarrotes-api-1.0.0.jar"]