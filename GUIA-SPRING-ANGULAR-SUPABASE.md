# Guía de Despliegue y Configuración - Spring Boot, Angular y Supabase

Esta guía detalla los pasos para configurar el entorno local y desplegar la aplicación en la nube, cumpliendo con los estándares de evaluación.

## 1. Configuración de la Base de Datos (Supabase)
Supabase proporciona PostgreSQL gestionado en la nube.
1. Crea un proyecto en [Supabase](https://supabase.com/).
2. Ve a **SQL Editor** y ejecuta todo el contenido del archivo `supabase-setup.sql` ubicado en la raíz del proyecto. Esto creará las tablas, vistas y funciones necesarias (`descontar_stock_fefo`, `normalizar_texto`, etc.).
3. Ve a **Project Settings > Database** y copia tus credenciales.
4. Asegúrate de desactivar el modo `transaction` en el Connection Pooler si usas el puerto 6543, o utiliza la bandera `prepareThreshold=0` (que ya está configurada en `application.yml`).

## 2. Configuración del Backend (Spring Boot)
El backend requiere **Java 21**. Asegúrate de tener configurada tu variable de entorno `JAVA_HOME` apuntando al JDK 21.

1. Navega a la carpeta `backend/`.
2. Crea un archivo `.env` basado en `.env.example` (o utiliza el `.env` en la raíz) con las siguientes variables:
   ```env
   SUPABASE_DB_HOST=aws-0-REGION.pooler.supabase.com
   SUPABASE_DB_PORT=6543
   SUPABASE_DB_NAME=postgres
   SUPABASE_DB_USER=postgres.tu-project-id
   SUPABASE_DB_PASSWORD=tu-password-seguro
   SUPABASE_DB_SSLMODE=require
   JWT_SECRET=tu-secreto-jwt-largo-y-seguro-minimo-32-caracteres
   CORS_ORIGINS=http://localhost:4200
   ```
3. Ejecuta el backend localmente para probar:
   ```bash
   .\mvnw.cmd spring-boot:run
   ```
   *Nota: Si tienes errores de compilación con `"""`, asegúrate de que tu `JAVA_HOME` sea Java 21, ya que Java 8 no soporta Text Blocks.*

## 3. Configuración del Frontend (Angular 19)
1. Navega a la carpeta `frontend/`.
2. Instala dependencias: `npm install`
3. Inicia el servidor de desarrollo: `npm start`
4. Accede a `http://localhost:4200`.

## 4. Despliegue en la Nube (Producción)

### Despliegue del Backend (Render / Railway)
Recomendamos usar **Render** o **Railway** para el backend de Spring Boot.
1. Crea un nuevo **Web Service**.
2. Conecta tu repositorio de GitHub.
3. Establece el Root Directory en `backend`.
4. Comando de construcción (Build Command): `./mvnw clean package -DskipTests`
5. Comando de inicio (Start Command): `java -jar target/abarrotes-api-1.0.0.jar`
6. Añade las variables de entorno (`SUPABASE_DB_HOST`, `SUPABASE_DB_PASSWORD`, etc.) en el panel de configuración del hosting. No olvides actualizar `CORS_ORIGINS` con la URL de tu frontend en producción.

### Despliegue del Frontend (Vercel / Netlify)
Recomendamos **Vercel** o **Netlify** para aplicaciones Angular.
1. En `frontend/src/environments/environment.prod.ts`, asegúrate de que `apiUrl` apunte a la URL de tu backend en producción (ej. `https://tu-backend.onrender.com/api`).
2. Conecta tu repositorio en Vercel.
3. Establece el Root Directory en `frontend`.
4. Framework Preset: **Angular**
5. Build Command: `npm run build`
6. Output Directory: `dist/inventario/browser`
7. Despliega la aplicación.

## 5. Solución de Errores Comunes (Troubleshooting)

**Error 500 (Internal Server Error) al crear un producto o ver inventario:**
- **Causa:** Generalmente ocurre si el esquema de base de datos no está sincronizado. Por ejemplo, si falta una función, vista o tabla requerida en Supabase, o si la conexión a la base de datos expiró.
- **Solución implementada:** Se ha mejorado el `GlobalExceptionHandler` en Spring Boot. Ahora los errores 500 devolverán el mensaje exacto de la excepción en la respuesta JSON (ej. `Error interno: relation "categoria" does not exist`). 
- Revisa la pestaña de **Red (Network)** en las herramientas de desarrollador de tu navegador para ver el mensaje exacto de error que arroja el backend y tomar acción en tu base de datos de Supabase.
