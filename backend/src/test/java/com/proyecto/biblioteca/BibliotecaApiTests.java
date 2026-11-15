package com.proyecto.biblioteca;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class BibliotecaApiTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testRootEndpointInfo() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aplicacion").value("API REST - Sistema de Biblioteca Integral"))
                .andExpect(jsonPath("$.estado").value("Activo"));
    }

    @Test
    void testListarLibros() throws Exception {
        mockMvc.perform(get("/api/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isbn").isNotEmpty())
                .andExpect(jsonPath("$[0].titulo").isNotEmpty());
    }

    @Test
    void testCrearLibroPorIsbn() throws Exception {
        String jsonLibro = """
                {
                    "isbn": "978-8437604183",
                    "titulo": "Pedro Páramo",
                    "sinopsis": "Novela de realismo mágico mexicano",
                    "idAutor": 3,
                    "idCategoria": 1,
                    "idEditorial": 2,
                    "anioPublicacion": 1955
                }
                """;

        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibro))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isbn").value("978-8437604183"))
                .andExpect(jsonPath("$.titulo").value("Pedro Páramo"));
    }

    @Test
    void testListarEjemplaresYCrearEjemplar() throws Exception {
        mockMvc.perform(get("/api/ejemplares"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].libro").isNotEmpty());

        String jsonEjemplar = """
                {
                    "isbn": "978-0307474728",
                    "numeroCopia": 3,
                    "ubicacion": "Estantería A-1, Nivel 2",
                    "estadoConservacion": "EXCELENTE",
                    "estado": "DISPONIBLE"
                }
                """;

        mockMvc.perform(post("/api/ejemplares")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEjemplar))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isbn").value("978-0307474728"))
                .andExpect(jsonPath("$.libro.titulo").value("Cien años de soledad"));
    }

    @Test
    void testListarUsuariosYCrearUsuario() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        String jsonUsuario = """
                {
                    "dni": "88776655",
                    "nombre": "Ana Morales",
                    "email": "ana.morales@example.com",
                    "telefono": "+51 988776655",
                    "direccion": "Calle Mayor 100",
                    "tipoUsuario": "ESTUDIANTE",
                    "estado": "ACTIVO"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUsuario))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Ana Morales"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void testRegistrarPrestamoExitoso() throws Exception {
        String jsonPrestamo = """
                {
                    "idEjemplar": 2,
                    "idUsuario": 1
                }
                """;

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPrestamo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.idEjemplar").value(2))
                .andExpect(jsonPath("$.fechaHoraPrestamo").isNotEmpty())
                .andExpect(jsonPath("$.fechaHoraDevolucionEsperada").isNotEmpty());
    }

    @Test
    void testConsultarPrestamosDetalladosDeUsuario() throws Exception {
        // Consultar los libros prestados de un usuario con detalle de Libro y Ejemplar
        mockMvc.perform(get("/api/usuarios/1/prestamos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testDevolucionConCalculoDeMora() throws Exception {
        // Probar endpoint de devolución con fechaHoraDevolucionReal
        String jsonDevolucion = """
                {
                    "fechaHoraDevolucionReal": "2026-10-25T10:00:00",
                    "estadoConservacion": "BUENO",
                    "observaciones": "Entrega completada"
                }
                """;

        mockMvc.perform(put("/api/prestamos/1/devolver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDevolucion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DEVUELTO"))
                .andExpect(jsonPath("$.fechaHoraDevolucionReal").isNotEmpty());
    }

    @Test
    void testPagarMultaYDesbloquearUsuario() throws Exception {
        String jsonPago = """
                {
                    "montoPagado": 15.00,
                    "metodoPago": "EFECTIVO",
                    "comprobante": "REC-000999"
                }
                """;

        mockMvc.perform(post("/api/multas/1/pagar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPago))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMulta").value(1))
                .andExpect(jsonPath("$.montoPagado").value(15.00));
    }

    @Test
    void testMinimoCincoRegistrosPorCadaEntidad() throws Exception {
        mockMvc.perform(get("/api/autores")).andExpect(status().isOk()).andExpect(jsonPath("$[4]").exists());
        mockMvc.perform(get("/api/categorias")).andExpect(status().isOk()).andExpect(jsonPath("$[4]").exists());
        mockMvc.perform(get("/api/editoriales")).andExpect(status().isOk()).andExpect(jsonPath("$[4]").exists());
        mockMvc.perform(get("/api/libros")).andExpect(status().isOk()).andExpect(jsonPath("$[4]").exists());
        mockMvc.perform(get("/api/ejemplares")).andExpect(status().isOk()).andExpect(jsonPath("$[4]").exists());
        mockMvc.perform(get("/api/usuarios")).andExpect(status().isOk()).andExpect(jsonPath("$[4]").exists());
        mockMvc.perform(get("/api/prestamos")).andExpect(status().isOk()).andExpect(jsonPath("$[4]").exists());
        mockMvc.perform(get("/api/multas")).andExpect(status().isOk()).andExpect(jsonPath("$[4]").exists());
    }

    @Test
    void testEndpointsDeReportes() throws Exception {
        mockMvc.perform(get("/api/reportes/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.catalogo.totalTitulosIsbn").isNumber())
                .andExpect(jsonPath("$.catalogo.totalEjemplaresFisicos").isNumber())
                .andExpect(jsonPath("$.usuarios.totalRegistrados").isNumber())
                .andExpect(jsonPath("$.prestamos.totalRegistrados").isNumber())
                .andExpect(jsonPath("$.multas.totalMultas").isNumber());

        mockMvc.perform(get("/api/reportes/libros-por-categoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos").isMap());

        mockMvc.perform(get("/api/reportes/prestamos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos").isMap());
    }
}
