# Wiki Oficial - Sistema de Gestion de Biblioteca

Bienvenido a la documentacion tecnica y operativa del Sistema de Gestion de Biblioteca, desarrollado para la asignatura de Desarrollo Web Integrado.

Esta Wiki contiene el compendio completo de diseno de software, arquitectura en capas, especificacion del modelo de persistencia relacional con PostgreSQL, logica transaccional de prestamos y sanciones, directivas de seguridad con Spring Security y tokens JWT, y el manual detallado de la API RESTful.

---

## Tabla de Navegacion de la Wiki

* [[1. Arquitectura y Tecnologias|Arquitectura-y-Tecnologias]]
  * Vision general de la arquitectura por capas.
  * Tecnologias utilizadas (Java 21, Spring Boot, PostgreSQL, Spring Security, JWT, Lombok, Bean Validation).
  * Estructura de paquetes del backend.

* [[2. Modelo de Datos y Base de Datos|Modelo-de-Datos-y-Base-de-Datos]]
  * Diferenciacion conceptual: Libro (ISBN) vs. Ejemplar Fisico.
  * Diagrama Entidad-Relacion (DER).
  * Mapeo de entidades JPA y esquema relacional en PostgreSQL.

* [[3. Ciclo de Prestamos y Multas|Ciclo-de-Prestamos-y-Multas]]
  * Gestion temporal con LocalDateTime (fecha/hora exacta).
  * Reglas de negocio para devoluciones puntuales y tardias.
  * Calculo automatico de mora en horas y dias.
  * Circuito de pago de multas, comprobantes y desbloqueo de usuarios.

* [[4. Seguridad y JWT|Seguridad-y-JWT]]
  * Flujo de autenticacion stateless con JSON Web Tokens.
  * Encriptacion de credenciales con BCrypt.
  * Jerarquia de roles (ADMIN, BIBLIOTECARIO, ESTUDIANTE, DOCENTE).
  * Configuracion CORS y filtros de seguridad.

* [[5. Manual de Endpoints API|Manual-de-Endpoints-API]]
  * Especificacion tecnica de todas las rutas REST.
  * Estructuras de peticion y respuesta en formato JSON.
  * Codigos de estado HTTP estandarizados (200, 201, 204, 400, 401, 404, 409).

* [[6. Instalacion y Postman|Instalacion-y-Postman]]
  * Requisitos previos del entorno de desarrollo.
  * Guia de configuracion de PostgreSQL.
  * Compilacion y ejecucion con Spring Boot.
  * Manual de importacion y uso de la coleccion de Postman.

---

## Ficha del Proyecto
* **Institucion:** Universidad
* **Asignatura:** Desarrollo Web Integrado
* **Version del Release:** v2.0.0
* **Repositorio:** [Jamesllm/proyecto-biblioteca](https://github.com/Jamesllm/proyecto-biblioteca)
