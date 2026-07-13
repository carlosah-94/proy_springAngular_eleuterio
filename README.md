# Sistema de Gestión de Inventario - Abarrotes Eleuterio

## Descripción
Es una plataforma web diseñada para optimizar la logística y el control de inventario de una tienda de abarrotes. Permite gestionar productos, registrar ventas y monitorear el stock mediante un dashboard intuitivo.

## Tecnologías
- **Frontend:** Angular 19
- **Backend:** Spring Boot 3.4 (API REST + JWT)
- **Base de Datos:** PostgreSQL (Supabase)
- **Control de Versiones:** Git & GitHub (Git Flow)

> **Guía completa de configuración:** ver [GUIA-SPRING-ANGULAR-SUPABASE.md](./GUIA-SPRING-ANGULAR-SUPABASE.md)

## Instalación y Ejecución Local (Spring Boot + Angular)

### 1. Base de datos Supabase
Ejecuta el script `supabase-setup.sql` en el SQL Editor de Supabase.

### 2. Backend (Spring Boot — puerto 8080)
```powershell
cd backend
# Configura backend/.env según backend/.env.example
.\mvnw.cmd spring-boot:run
```

### 3. Frontend (Angular — puerto 4200)
```powershell
cd frontend
npm install
npm start
```

Abre `http://localhost:4200`

### Credenciales
- Email: `eleuterio@abarrotes.com`
- Contraseña: `Eleuterio2024!`

---

## Versión anterior (Node.js)
El código legacy Express/HTML sigue en `server.js` y `public/` como referencia.

**Sprint 2 - Mejoras y control de inventario**

- **Nuevas Características:** Semáforo de stock (Prevención de ventas sin unidades), Alertas de vencimiento de productos, Filtrado y búsqueda global en tiempo real, Tipos de productos dinámicos (Retornables, etc.).
- **Decisiones Técnicas:** Se restringió el modelo de usuarios a un entorno de negocio cerrado (Privado) eliminando registros públicos.

## Sprint 3

Durante el Sprint 3, se implementaron diversas mejoras funcionales y de experiencia de usuario enfocadas en el control financiero y gestión de inventario:

- **Buscador Inteligente:** Búsqueda flexible en el inventario y ventas (ignora mayúsculas y tildes).
- **Gestión de Notificaciones:** Sistema dinámico de alertas tempranas sobre stock bajo y productos próximos a caducar.
- **Control de Vencimientos y Categorías:** Posibilidad de agregar categorías al vuelo (ComboBox) y registrar fechas de vencimiento específicas por lote al reabastecer productos desde proveedores.
- **Generación de Comprobantes:** Exportación de boletas de venta en formato "ticket" optimizado para impresión térmica (mediante jsPDF).
- **Reportes Automatizados:** Generación y descarga automática de reportes PDF (Ventas Semanales y Gastos de Proveedores) todos los domingos a medianoche.
- **Dashboard Dinámico:** Tarjetas funcionales de resumen (Ventas del día, Stock Crítico) con reseteo automático a medianoche.
- **Ultimas correciones:** Se soluciono la enredadera de las ramas que fueron unidas por accidente a la rama main, ahora apuntan a la rama develop, visualizando los cambios.

## Configuración de Base de Datos (Sprint 4)

El proyecto utiliza **PostgreSQL** a través de **Supabase** como base de datos.

### Pasos para configurar:

1. Solicita al equipo las credenciales de Supabase (URL y Service Key)
2. Copia el archivo de ejemplo: `copy .env.example .env` (Windows) o `cp .env.example .env` (Mac/Linux)
3. Abre el archivo `.env` y rellena los valores reales:
   - `SUPABASE_URL` — URL del proyecto Supabase
   - `SUPABASE_SERVICE_KEY` — Service Role Key de Supabase
   - `JWT_SECRET` — Clave secreta para tokens JWT (cualquier texto largo y aleatorio)
4. Instala las dependencias: `npm install`
5. Crea el usuario inicial: `npm run seed`
6. Ejecuta el servidor: `npm run dev`

### Credenciales de acceso
- Email: `eleuterio@abarrotes.com`
- Contraseña: `Eleuterio2024!`

## Equipo de Desarrollo
- **Connery Diaz** - Product Owner
- **Carlos Atahua** - Scrum Master
- **Avril Soriano** - Desarrolladora
- **Marco Martínez** - Desarrollador
- **Ricardo De La Cruz** - Desarrollador