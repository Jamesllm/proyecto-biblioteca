package com.proyecto.biblioteca;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .andExpect(jsonPath("$.aplicacion").value("API REST - Sistema de Biblioteca"))
                .andExpect(jsonPath("$.estado").value("Activo"));
    }

    @Test
    void testListarLibros() throws Exception {
        mockMvc.perform(get("/api/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].titulo").isNotEmpty());
    }

    @Test
    void testCrearLibro() throws Exception {
        String jsonLibro = """
                {
                    "titulo": "Pedro Páramo",
                    "isbn": "978-8437604183",
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
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.titulo").value("Pedro Páramo"))
                .andExpect(jsonPath("$.disponible").value(true));
    }

    @Test
    void testListarUsuariosYCrearUsuario() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        String jsonUsuario = """
                {
                    "nombre": "Ana Morales",
                    "email": "ana.morales@example.com",
                    "telefono": "+51 988776655",
                    "direccion": "Calle Mayor 100"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUsuario))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Ana Morales"));
    }

    @Test
    void testRegistrarPrestamoExitoso() throws Exception {
        String jsonPrestamo = """
                {
                    "idLibro": 1,
                    "idUsuario": 2
                }
                """;

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPrestamo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.idLibro").value(1));
    }

    @Test
    void testMinimoCincoRegistrosPorCadaEntidad() throws Exception {
        // Verificar que cada endpoint cuenta con mínimo 5 registros iniciales
        mockMvc.perform(get("/api/autores")).andExpect(status().isOk()).andExpect(jsonPath("$[4]").exists());
        mockMvc.perform(get("/api/categorias")).andExpect(status().isOk()).andExpect(jsonPath("$[4]").exists());
        mockMvc.perform(get("/api/editoriales")).andExpect(status().isOk()).andExpect(jsonPath("$[4]").exists());
        mockMvc.perform(get("/api/libros")).andExpect(status().isOk()).andExpect(jsonPath("$[4]").exists());
        mockMvc.perform(get("/api/usuarios")).andExpect(status().isOk()).andExpect(jsonPath("$[4]").exists());
        mockMvc.perform(get("/api/prestamos")).andExpect(status().isOk()).andExpect(jsonPath("$[4]").exists());
        mockMvc.perform(get("/api/multas")).andExpect(status().isOk()).andExpect(jsonPath("$[4]").exists());
    }

    @Test
    void testActualizarConMetodoPut() throws Exception {
        // Probar PUT en Autor
        String jsonAutorPut = """
                {
                    "nombre": "Gabriel García Márquez Actualizado",
                    "nacionalidad": "Colombiana",
                    "biografia": "Premio Nobel de Literatura y autor legendario."
                }
                """;
        mockMvc.perform(put("/api/autores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAutorPut))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Gabriel García Márquez Actualizado"));

        // Probar PUT en Libro
        String jsonLibroPut = """
                {
                    "titulo": "Cien años de soledad (Edición Especial)",
                    "isbn": "978-0307474728",
                    "idAutor": 1,
                    "idCategoria": 1,
                    "idEditorial": 1,
                    "anioPublicacion": 1967
                }
                """;
        mockMvc.perform(put("/api/libros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibroPut))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Cien años de soledad (Edición Especial)"));
    }

    @Test
    void testEndpointsDeReportes() throws Exception {
        mockMvc.perform(get("/api/reportes/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.catalogo.totalLibros").isNumber())
                .andExpect(jsonPath("$.usuarios.totalRegistrados").isNumber())
                .andExpect(jsonPath("$.prestamos.totalRegistrados").isNumber())
                .andExpect(jsonPath("$.multas.totalMultas").isNumber());

        mockMvc.perform(get("/api/reportes/libros-por-categoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distribucion").isMap());

        mockMvc.perform(get("/api/reportes/prestamos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detalle").isArray());
    }

    @Test
    void testValidacionFallaCuandoDatosInvalidos() throws Exception {
        String jsonLibroInvalido = """
                {
                    "titulo": "",
                    "isbn": ""
                }
                """;

        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibroInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores").exists())
                .andExpect(jsonPath("$.errores.titulo").isNotEmpty());
    }
}
