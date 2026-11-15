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

El sistema opera bajo un esquema de autenticacion sin estado (Stateless Session) mediante **Spring Security** y **JJWT**:
* **Firma Criptografica:** Tokens firmados mediante algoritmo HMAC-SHA256 con tiempo de expiracion configurable (24 horas).
* **Cabecera de Autorizacion:** Las peticiones protegidas deben incluir la cabecera HTTP:
  ```http
  Authorization: Bearer <token_jwt>
  ```
* **Respuesta en caso de omision:** Si no se incluye el token o este expiro/invalido, el servidor responde con codigo HTTP `401 Unauthorized`.
* **Roles de Acceso:** `ROLE_ADMIN`, `ROLE_BIBLIOTECARIO`, `ROLE_ESTUDIANTE`, `ROLE_DOCENTE`.

### Credenciales Precargadas de Demostracion
| Rol | Correo Electronico | Contraseña |
|---|---|---|
| Administrador | admin@biblioteca.com | admin123 |
| Bibliotecario | bibliotecario@biblioteca.com | biblio123 |
| Estudiante Lector | carlos.mendoza@email.com | carlos123 |
| Docente Lector | lucia.fernandez@email.com | lucia123 |

---

## Matriz de Autenticacion por Endpoint (Publicos vs. JWT Requerido)

| Modulo / Recurso | Metodo HTTP | Ruta | ¿Requiere Token JWT? | Descripcion / Permisos |
|---|---|---|:---:|---|
| **Bienvenida** | `GET` | `/` | ❌ No (Publico) | Estado del servidor |
| **Autenticacion** | `POST` | `/api/auth/login` | ❌ No (Publico) | Inicia sesion y retorna Token JWT |
| | `POST` | `/api/auth/register` | ❌ No (Publico) | Registra nuevo lector |
| | `GET` | `/api/auth/me` | ✅ **SI (JWT)** | Perfil del usuario autenticado |
| **Catalogo de Libros** | `GET` | `/api/libros` | ❌ No (Publico) | Listado general de obras |
| | `GET` | `/api/libros/{isbn}` | ❌ No (Publico) | Detalle de libro por ISBN |
| | `GET` | `/api/libros/{isbn}/ejemplares` | ❌ No (Publico) | Ejemplares asociados |
| | `GET` | `/api/libros/{isbn}/ejemplares/disponibles` | ❌ No (Publico) | Ejemplares disponibles |
| | `POST` | `/api/libros` | ✅ **SI (JWT)** | Crear libro en el catalogo |
| | `PUT` | `/api/libros/{isbn}` | ✅ **SI (JWT)** | Actualizar libro |
| | `DELETE` | `/api/libros/{isbn}` | ✅ **SI (JWT)** | Eliminar libro |
| **Ejemplares Fisicos** | `GET` | `/api/ejemplares/**` | ❌ No (Publico) | Consulta de stock y ubicaciones |
| | `POST` | `/api/ejemplares` | ✅ **SI (JWT)** | Alta de nuevo ejemplar fisico |
| | `PUT` | `/api/ejemplares/{id}` | ✅ **SI (JWT)** | Modificar ejemplar (estado/ubicacion) |
| | `DELETE` | `/api/ejemplares/{id}` | ✅ **SI (JWT)** | Baja de ejemplar |
| **Autores, Categorias, Editoriales** | `GET` | `/api/autores/**`, `/api/categorias/**`, `/api/editoriales/**` | ❌ No (Publico) | Consulta de clasificaciones |
| | `POST`, `PUT`, `DELETE` | `/api/autores/**`, `/api/categorias/**`, `/api/editoriales/**` | ✅ **SI (JWT)** | Mantenimiento de clasificaciones |
| **Reportes y Metricas** | `GET` | `/api/reportes/**` | ❌ No (Publico) | Dashboard, estadisticas y metricas |
| **Prestamos y Circulacion** | `GET` | `/api/prestamos` | ✅ **SI (JWT)** | Listar prestamos |
| | `GET` | `/api/prestamos/{id}` | ✅ **SI (JWT)** | Detalle de prestamo |
| | `GET` | `/api/prestamos/usuario/{idUsuario}` | ✅ **SI (JWT)** | Prestamos por usuario |
| | `POST` | `/api/prestamos` | ✅ **SI (JWT)** | Registrar prestamo |
| | `PUT` / `POST` | `/api/prestamos/{id}/devolver` | ✅ **SI (JWT)** | Registrar devolucion y calcular mora |
| **Multas y Sanciones** | `GET` | `/api/multas/**` | ✅ **SI (JWT)** | Listar y consultar multas |
| | `POST` | `/api/multas` | ✅ **SI (JWT)** | Emision manual de multa |
| | `POST` | `/api/multas/{id}/pagar` | ✅ **SI (JWT)** | Pago de sancion y desbloqueo |
| **Pagos de Multas** | `GET` | `/api/pagos-multas/**` | ✅ **SI (JWT)** | Auditoria y comprobantes de pago |
| **Reservas de Libros** | `GET`, `POST`, `PUT` | `/api/reservas/**` | ✅ **SI (JWT)** | Gestion de lista de reservas |
| **Usuarios** | `GET`, `POST`, `PUT`, `DELETE` | `/api/usuarios/**` | ✅ **SI (JWT)** | Administracion y ficha de lectores |

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