# Backend MRP - E-commerce

## Requisitos previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- **Java 17** o superior
  - Verifica la instalación: `java -version`
  - Descarga desde: https://www.oracle.com/java/technologies/downloads/

- **PostgreSQL 12** o superior
  - Descarga desde: https://www.postgresql.org/download/
  - Asegúrate de que el servidor PostgreSQL esté corriendo

- **Gradle** (incluido en el proyecto con Gradle Wrapper)
  - El proyecto usa Gradle Wrapper, así que no necesitas instalarlo globalmente

## Configuración inicial

### 1. Clonar o descargar el proyecto
```bash
cd backend
```

### 2. Crear la base de datos PostgreSQL
Conecta a PostgreSQL y crea una base de datos para el proyecto:

```bash
psql -U postgres
```

En la consola de PostgreSQL:
```sql
CREATE DATABASE mrp_db;
```

### 3. Configurar variables de entorno

Crea un archivo `.env` en la raíz del proyecto backend con las siguientes variables:

```env
# Puerto de la aplicación
SERVER_PORT=8080

# Configuración de la base de datos PostgreSQL
DB_URL=jdbc:postgresql://localhost:5432/mrp_db
DB_USERNAME=postgres
DB_PASSWORD=tu_contraseña_postgres

# Hibernate solo valida el esquema existente
JPA_DDL_AUTO=validate

# Configuración JWT
JWT_SECRET=tu_clave_secreta_jwt_muy_segura_y_larga
JWT_EXPIRATION=86400000
```

**Nota:** Reemplaza los valores con tus configuraciones reales.

### 4. Migraciones de base de datos

El proyecto usa **Flyway** para crear y versionar el esquema.

- Las migraciones SQL están en `src/main/resources/db/migration`.
- Con una base vacía, al arrancar la aplicación se ejecuta `V1__bootstrap_schema.sql`.
- Esa migración crea las tablas base del modelo actual:
  - `users`
  - `role`
  - `permission`
  - `permission_role`
- También contempla renombres desde el esquema legado (`permiso_rol`, `rol_id`, `permiso_id`, `nombre`) para entornos existentes.

No necesitas crear tablas a mano.

## Ejecutar el proyecto

### Opción 1: Usando Gradle Wrapper (recomendado)

```bash
# Desde el directorio backend
./gradlew bootRun
```

O si estás en Windows:
```bash
gradlew.bat bootRun
```

### Opción 2: Compilar y ejecutar el JAR

```bash
# Compilar el proyecto
./gradlew build

# Ejecutar el JAR generado
java -jar build/libs/backend-mrp-0.0.1-SNAPSHOT.jar
```

### Opción 3: Desde tu IDE

Si usas IntelliJ IDEA o Eclipse:

1. Abre el proyecto en tu IDE
2. Ejecuta la clase principal: `SpringBootApplication`
3. O usa la opción "Run" del IDE con Spring Boot Run Configuration

## Verificar que el servidor está corriendo

Una vez que el servidor inicie, deberías ver algo como:

```
Started SpringBootApplication in X.XXX seconds
```

**Acceder a la aplicación:**
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/docs
- API Docs (JSON): http://localhost:8080/api/v3/api-docs

## Dependencias principales

- **Spring Boot 3.3.4** - Framework principal
- **Spring Data JPA** - Acceso a datos
- **Spring Security** - Seguridad y autenticación
- **JWT (JSON Web Tokens)** - Autenticación basada en tokens
- **PostgreSQL** - Base de datos relacional
- **Springdoc OpenAPI** - Documentación automática con Swagger
- **Lombok** - Reducción de código boilerplate
- **Hibernate Validator** - Validación de datos

## Estructura del proyecto

```
src/
├── main/
│   ├── java/eccomerce/
│   └── resources/
│       └── application.properties
└── test/
    └── java/eccomerce/
```

## Comandos útiles

```bash
# Compilar el proyecto
./gradlew clean build

# Ejecutar pruebas
./gradlew test

# Ejecutar con modo desarrollo
./gradlew bootRun --args='--spring.profiles.active=dev'

# Ver tareas disponibles
./gradlew tasks
```

## Solución de problemas

### Error de conexión a la base de datos
- Verifica que PostgreSQL está corriendo
- Comprueba que los datos de conexión en `.env` son correctos
- Asegúrate que la base de datos existe

### Error de puerto en uso
- Si el puerto 8080 está en uso, cambia `SERVER_PORT` en el archivo `.env`

### Java version mismatch
- Verifica que tienes Java 17: `java -version`
- Configura la variable de entorno `JAVA_HOME` correctamente

## Documentación adicional

- [Documentación oficial de Spring Boot](https://spring.io/projects/spring-boot)
- [Guía de Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [JWT en Java](https://github.com/jwtk/jjwt)
