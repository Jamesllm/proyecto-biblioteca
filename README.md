# Sistema de Gestion de Biblioteca - API RESTful

Proyecto de backend desarrollado para la asignatura de Desarrollo Web Integrado. Provee una API RESTful modular, segura y escalable para la gestion del acervo bibliografico, administracion de ejemplares fisicos, padron de usuarios lectores, circuito de prestamos y fiscalizacion automatica de sanciones economicas.

---

## Ficha Tecnica del Proyecto

* **Lenguaje de Programacion:** Java 21 LTS
* **Framework Principal:** Spring Boot 4.x / 3.x (Spring Web MVC)
* **Persistencia y ORM:** Spring Data JPA / Hibernate
* **Motor de Base de Datos:** PostgreSQL 14+
* **Seguridad y Control de Acceso:** Spring Security 6.x + JSON Web Tokens (JJWT 0.12.x)
* **Cifrado de Credenciales:** BCrypt Password Encoder
* **Herramientas de Soporte:** Project Lombok, Jakarta Bean Validation
* **Control de Versiones:** Git / GitHub

---

## Arquitectura del Software

El sistema implementa el patron arquitectonico en capas (Layered Architecture), asegurando una estricta separacion de responsabilidades, bajo acoplamiento y alta mantenibilidad:

* **Capa de Presentacion (Controllers):** Controladores REST (`@RestController`) que exponen los endpoints HTTP, gestionan peticiones/respuestas en formato JSON y validan entradas de datos con Jakarta Validation.
* **Capa de Seguridad (Security):** Filtros de interceptacion (`OncePerRequestFilter`), validacion de cabeceras Bearer JWT y politicas de autorizacion basadas en roles.
* **Capa de Logica de Negocio (Services):** Servicios transaccionales (`@Service`, `@Transactional`) que ejecutan las reglas operativas, calculo automatico de moras y estados de inventario.
* **Capa de Acceso a Datos (Repositories):** Interfaces que extienden `JpaRepository` para operaciones CRUD y consultas derivadas hacia PostgreSQL.
* **Capa de Dominio (Entities & DTOs):** Modelos de entidad JPA (`@Entity`) y objetos de transferencia de datos (`DTO`) para desacoplar el contrato de la API del esquema de almacenamiento.

---

## Modelo de Dominio y Reglas de Negocio

### 1. Catalogacion por ISBN vs. Ejemplares Fisicos
* **Libro:** Representa la ficha bibliografica de la obra intelectual. Su identificador principal unico es el codigo `isbn` (String), prescindiendo de identificadores numericos artificiales.
* **Ejemplar:** Representa cada copia fisica tangible en estanteria. Un mismo ISBN puede contener multiples ejemplares con su respectivo numero de copia, ubicacion fisica y estado (`DISPONIBLE`, `PRESTADO`, `EN_MANTENIMIENTO`, `DADO_DE_BAJA`).
* **Circulacion:** Los prestamos se asignan directamente a un ejemplar especifico, garantizando el control del stock individual.

### 2. Control Temporal con LocalDateTime y Calculo Automatico de Mora
Cada transaccion de prestamo registra instantes precisos:
* `fechaHoraPrestamo`: Momento del retiro del ejemplar.
* `fechaHoraDevolucionEsperada`: Plazo limite establecido (14 dias calendario por defecto).
* `fechaHoraDevolucionReal`: Momento efectivo de recepcion fisica.

Al registrar una devolucion:
* Si `fechaHoraDevolucionReal <= fechaHoraDevolucionEsperada`: El prestamo se marca como `DEVUELTO` y el ejemplar retorna a `DISPONIBLE` sin penalizaciones.
* Si `fechaHoraDevolucionReal > fechaHoraDevolucionEsperada`: Se calcula automaticamente la diferencia en dias y horas de retraso, emitiendo una `Multa` por mora (calculada a razon de S/. 2.50 por dia excedido) y pasando al usuario al estado `SANCIONADO` (bloqueado para nuevos prestamos hasta la liquidacion de su deuda).

### 3. Gestion de Cobros y Desbloqueo
Al procesar el pago de una multa via `POST /api/multas/{id}/pagar`:
* Se registra la transaccion en `pagos_multas` con comprobante de pago emitido.
* La multa pasa al estado `PAGADA`.
* Si el usuario no registra mas sanciones pendientes, su estado se conmuta automaticamente a `ACTIVO`.

---

## Seguridad y Autenticacion Stateless (JWT)

El sistema opera bajo un esquema de autenticacion sin estado (Stateless Session):
* **Firma Criptografica:** Tokens firmados mediante algoritmo HMAC-SHA256 con tiempo de expiracion configurable.
* **Cabecera de Autorizacion:** Las peticiones protegidas deben incluir la cabecera HTTP `Authorization: Bearer <token>`.
* **Roles de Acceso:** `ROLE_ADMIN`, `ROLE_BIBLIOTECARIO`, `ROLE_ESTUDIANTE`, `ROLE_DOCENTE`.

### Credenciales Precargadas de Demostracion
| Rol | Correo Electronico | Contraseña |
|---|---|---|
| Administrador | admin@biblioteca.com | admin123 |
| Bibliotecario | bibliotecario@biblioteca.com | biblio123 |
| Estudiante Lector | carlos.mendoza@email.com | carlos123 |
| Docente Lector | lucia.fernandez@email.com | lucia123 |

---

## Catalogo de Endpoints Principales

### Autenticacion (`/api/auth`)
* `POST /api/auth/login`: Autentica credenciales y emite el Token JWT.
* `POST /api/auth/register`: Registra un nuevo usuario en la base de datos con contraseña cifrada.
* `GET /api/auth/me`: Retorna la informacion del perfil del usuario autenticado.

### Catalogo de Libros y Ejemplares (`/api/libros`, `/api/ejemplares`)
* `GET /api/libros`: Listado general de obras registradas (Acceso Publico).
* `GET /api/libros/{isbn}`: Consulta de obra por codigo ISBN (Acceso Publico).
* `GET /api/libros/{isbn}/ejemplares`: Listado de copias fisicas asociadas a un ISBN.
* `GET /api/libros/{isbn}/ejemplares/disponibles`: Copias disponibles para prestamo inmediato.
* `POST /api/libros`: Registro de nueva obra intelectual (Requiere Token).
* `POST /api/ejemplares`: Alta de nueva copia fisica en estanteria (Requiere Token).

### Usuarios y Consultas Detalladas (`/api/usuarios`)
* `GET /api/usuarios`: Listado de usuarios registrados.
* `GET /api/usuarios/{id}/prestamos`: Historial completo enriquecido de prestamos del usuario.
* `GET /api/usuarios/{id}/prestamos/activos`: Libros que el usuario tiene actualmente en su poder.
* `GET /api/usuarios/{id}/multas`: Sanciones asociadas al usuario.

### Circulacion de Prestamos y Devoluciones (`/api/prestamos`)
* `GET /api/prestamos`: Listado de prestamos registrados.
* `POST /api/prestamos`: Registro de prestamo validando usuario activo y ejemplar disponible.
* `PUT /api/prestamos/{id}/devolver`: Registro de devolucion fisica con calculo automatico de mora.

### Finanzas y Sanciones (`/api/multas`, `/api/pagos-multas`)
* `GET /api/multas`: Listado de multas registradas.
* `GET /api/multas/usuario/{id}/pendientes`: Consulta de deuda pendiente de un lector.
* `POST /api/multas/{id}/pagar`: Pago de multa, emision de comprobante y desbloqueo de usuario.
* `GET /api/pagos-multas`: Auditoria de transacciones de pago.

### Reservas y Reportes (`/api/reservas`, `/api/reportes`)
* `GET /api/reservas`: Cola de reservas activas.
* `POST /api/reservas`: Generacion de reserva para titulos sin ejemplares disponibles.
* `GET /api/reportes/dashboard`: Metricas consolidadas de catalogo, circulacion y recaudacion.

---

## Puesta en Marcha y Despliegue

### Requisitos del Entorno
* Java Development Kit (JDK) 21
* PostgreSQL Server 14 o superior
* Apache Maven 3.8+ (o wrapper `./mvnw`)

### 1. Base de Datos
Crear la base de datos en la instancia local o remota de PostgreSQL:
```sql
CREATE DATABASE biblioteca_db;
```

### 2. Configuracion de Parametros
Verificar las credenciales en `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/biblioteca_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

### 3. Compilacion y Ejecucion
```bash
# Compilacion del proyecto
./mvnw clean package -DskipTests

# Inicio del servidor
./mvnw spring-boot:run
```
El servicio quedara disponible en `http://localhost:8080`.

---