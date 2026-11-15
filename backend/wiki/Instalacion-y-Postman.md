# Instalacion, Configuracion y Pruebas con Postman

Esta pagina provee la guia paso a paso para levantar el proyecto en un entorno local y validar todos los endpoints mediante la coleccion de Postman.

---

## 1. Requisitos Previos

* **Java Development Kit (JDK):** Version 21 LTS instalada y configurada en el `PATH`.
* **PostgreSQL:** Version 14 o superior en ejecucion en el puerto `5432`.
* **Apache Maven:** Version 3.8+ o uso directo del wrapper incluido (`mvnw` / `mvnw.cmd`).
* **Postman Desktop:** Para la ejecucion y prueba de las peticiones HTTP.

---

## 2. Preparacion de la Base de Datos

Abra una terminal o cliente SQL (psql, pgAdmin, DBeaver) y cree la base de datos:

```sql
CREATE DATABASE biblioteca_db;
```

Si utiliza un usuario o contraseña distintos a los valores por defecto (`postgres` / `postgres`), modifique el archivo `src/main/resources/application.properties` o defina las siguientes variables de entorno:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/biblioteca_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

---

## 3. Compilacion y Ejecucion

En la raiz del proyecto, ejecute:

```bash
# En Windows (PowerShell / CMD)
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd spring-boot:run

# En Linux / macOS
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

Al iniciar por primera vez, el componente `DataInitializer` creara automaticamente todas las tablas en PostgreSQL e insertara los datos semilla de demostracion.

---

## 4. Guia de Uso de la Coleccion de Postman

1. Abra **Postman** y seleccione la opcion **Import**.
2. Arrastre o seleccione el archivo `ProyectoBiblioteca.postman_collection.json` ubicado en la raiz del proyecto.
3. **Flujo de Autenticacion Automatizado:**
   * Dirijase a la carpeta `0. Autenticacion & JWT`.
   * Ejecute la peticion `Login Administrador` o `Login Lector (Estudiante)`.
   * El script de prueba post-respuesta guardara automaticamente el Token JWT en la variable de entorno `token`.
4. **Ejecucion de Peticiones Protegidas:**
   * Todas las peticiones protegidas dentro de las carpetas de `Libros`, `Ejemplares`, `Usuarios`, `Prestamos` y `Multas` ya estan configuradas con el encabezado `Authorization: Bearer {{token}}`.
   * Puede ejecutar cualquier operacion de consulta, prestamo o devolucion sin configurar tokens manualmente.
