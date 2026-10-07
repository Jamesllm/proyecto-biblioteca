# Manual de Endpoints de la API REST

Esta pagina contiene la especificacion tecnica de todas las rutas HTTP expuestas por la API RESTful.

---

## 1. Modulo de Autenticacion (`/api/auth`)

### `POST /api/auth/login`
* **Nivel de Acceso:** Publico
* **Cuerpo de Solicitud (JSON):**
```json
{
  "email": "admin@biblioteca.com",
  "password": "admin123"
}
```
* **Respuesta Exitosa (`200 OK`):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "id": 1,
  "dni": "00000001",
  "nombre": "Administrador del Sistema",
  "email": "admin@biblioteca.com",
  "rol": "ROLE_ADMIN",
  "tipoUsuario": "DOCENTE",
  "estado": "ACTIVO"
}
```

### `POST /api/auth/register`
* **Nivel de Acceso:** Publico
* **Cuerpo de Solicitud (JSON):**
```json
{
  "dni": "73920194",
  "nombre": "Gonzalo Morales",
  "email": "gonzalo.morales@email.com",
  "password": "passwordSeguro123",
  "telefono": "+51 987112233",
  "direccion": "Av. Brasil 450",
  "tipoUsuario": "ESTUDIANTE",
  "rol": "ROLE_ESTUDIANTE"
}
```
* **Respuesta Exitosa (`201 Created`):** Retorna el `AuthResponseDTO` con el Token generado.

### `GET /api/auth/me`
* **Nivel de Acceso:** Requiere Bearer Token
* **Respuesta Exitosa (`200 OK`):** Retorna el objeto completo del perfil del usuario autenticado.

---

## 2. Modulo de Libros y Ejemplares (`/api/libros`, `/api/ejemplares`)

### `GET /api/libros`
* **Nivel de Acceso:** Publico
* **Respuesta Exitosa (`200 OK`):** Retorna el listado de todas las obras catalogadas.

### `GET /api/libros/{isbn}`
* **Nivel de Acceso:** Publico
* **Parametro de Ruta:** `isbn` (ej: `978-0307474728`)
* **Respuesta Exitosa (`200 OK`):**
```json
{
  "isbn": "978-0307474728",
  "titulo": "Cien años de soledad",
  "sinopsis": "Obra cumbre del realismo mágico latinoamericano.",
  "idAutor": 1,
  "idCategoria": 1,
  "idEditorial": 1,
  "anioPublicacion": 1967,
  "autor": {
    "id": 1,
    "nombre": "Gabriel García Márquez",
    "nacionalidad": "Colombiana"
  }
}
```

### `GET /api/libros/{isbn}/ejemplares`
* **Nivel de Acceso:** Publico
* **Respuesta Exitosa (`200 OK`):** Retorna todas las copias fisicas asociadas al ISBN.

### `GET /api/libros/{isbn}/ejemplares/disponibles`
* **Nivel de Acceso:** Publico
* **Respuesta Exitosa (`200 OK`):** Retorna solo las copias fisicas en estado `DISPONIBLE`.

### `POST /api/libros`
* **Nivel de Acceso:** Requiere Bearer Token
* **Cuerpo de Solicitud (JSON):**
```json
{
  "isbn": "978-8437604183",
  "titulo": "Pedro Páramo",
  "sinopsis": "Novela de realismo mágico mexicano",
  "idAutor": 3,
  "idCategoria": 1,
  "idEditorial": 2,
  "anioPublicacion": 1955
}
```

### `POST /api/ejemplares`
* **Nivel de Acceso:** Requiere Bearer Token
* **Cuerpo de Solicitud (JSON):**
```json
{
  "isbn": "978-0307474728",
  "numeroCopia": 3,
  "ubicacion": "Estantería A-1, Nivel 3",
  "estadoConservacion": "EXCELENTE",
  "estado": "DISPONIBLE"
}
```

---

## 3. Modulo de Usuarios y Consultas Detalladas (`/api/usuarios`)

### `GET /api/usuarios/{id}/prestamos`
* **Nivel de Acceso:** Requiere Bearer Token
* **Descripcion:** Retorna el historial completo de prestamos del usuario con los objetos completos de `Libro`, `Ejemplar` y `Multa`.
* **Respuesta Exitosa (`200 OK`):**
```json
[
  {
    "idPrestamo": 1,
    "estadoPrestamo": "ACTIVO",
    "fechaHoraPrestamo": "2026-10-02T17:30:00",
    "fechaHoraDevolucionEsperada": "2026-10-16T17:30:00",
    "fechaHoraDevolucionReal": null,
    "enMora": false,
    "horasRetraso": 0,
    "diasRetraso": 0,
    "horasRestantes": 216,
    "ejemplar": {
      "id": 1,
      "numeroCopia": 1,
      "ubicacion": "Estantería A-1, Nivel 1",
      "estadoConservacion": "EXCELENTE"
    },
    "libro": {
      "isbn": "978-0307474728",
      "titulo": "Cien años de soledad"
    },
    "multa": null
  }
]
```

### `GET /api/usuarios/{id}/prestamos/activos`
* **Nivel de Acceso:** Requiere Bearer Token
* **Descripcion:** Filtra unicamente los libros que el usuario tiene actualmente en su poder (en estado `ACTIVO` o `VENCIDO`).

---

## 4. Modulo de Prestamos y Devoluciones (`/api/prestamos`)

### `POST /api/prestamos`
* **Nivel de Acceso:** Requiere Bearer Token
* **Cuerpo de Solicitud (JSON):**
```json
{
  "idEjemplar": 2,
  "idUsuario": 3
}
```
* **Respuesta Exitosa (`201 Created`):** Registra el prestamo, fija 14 dias de plazo y conmuta el ejemplar a `PRESTADO`.

### `PUT /api/prestamos/{id}/devolver`
* **Nivel de Acceso:** Requiere Bearer Token
* **Cuerpo de Solicitud (JSON):**
```json
{
  "fechaHoraDevolucionReal": "2026-10-25T14:30:00",
  "estadoConservacion": "BUENO",
  "observaciones": "Devuelto con retraso de varios dias",
  "montoDanoExtra": 0.0
}
```
* **Respuesta Exitosa (`200 OK`):** Registra la devolucion. Si hubo retraso, emite la multa automatica y sanciona al usuario.

---

## 5. Modulo de Multas y Pagos (`/api/multas`, `/api/pagos-multas`)

### `GET /api/multas/usuario/{id}/pendientes`
* **Nivel de Acceso:** Requiere Bearer Token
* **Respuesta Exitosa (`200 OK`):** Lista las sanciones no saldadas del lector.

### `POST /api/multas/{id}/pagar`
* **Nivel de Acceso:** Requiere Bearer Token
* **Cuerpo de Solicitud (JSON):**
```json
{
  "montoPagado": 15.00,
  "metodoPago": "EFECTIVO",
  "comprobante": "REC-000501"
}
```
* **Respuesta Exitosa (`201 Created`):** Marca la multa como `PAGADA`, emite el comprobante y reactiva al usuario a `ACTIVO`.

---

## 6. Modulo de Reportes (`/api/reportes`)

* `GET /api/reportes/dashboard`: Resumen estadistico del sistema.
* `GET /api/reportes/libros-por-categoria`: Distribucion bibliografica por tematica.
* `GET /api/reportes/prestamos`: Metricas de circulacion por estado.
* `GET /api/reportes/financiero-multas`: Auditoria de ingresos y montos pendientes.
