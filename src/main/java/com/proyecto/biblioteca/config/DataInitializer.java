package com.proyecto.biblioteca.config;

import com.proyecto.biblioteca.models.*;
import com.proyecto.biblioteca.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AutorRepository autorRepository;
    private final CategoriaRepository categoriaRepository;
    private final EditorialRepository editorialRepository;
    private final LibroRepository libroRepository;
    private final EjemplarRepository ejemplarRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrestamoRepository prestamoRepository;
    private final MultaRepository multaRepository;
    private final PagoMultaRepository pagoMultaRepository;

    public DataInitializer(AutorRepository autorRepository,
                           CategoriaRepository categoriaRepository,
                           EditorialRepository editorialRepository,
                           LibroRepository libroRepository,
                           EjemplarRepository ejemplarRepository,
                           UsuarioRepository usuarioRepository,
                           PrestamoRepository prestamoRepository,
                           MultaRepository multaRepository,
                           PagoMultaRepository pagoMultaRepository) {
        this.autorRepository = autorRepository;
        this.categoriaRepository = categoriaRepository;
        this.editorialRepository = editorialRepository;
        this.libroRepository = libroRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.usuarioRepository = usuarioRepository;
        this.prestamoRepository = prestamoRepository;
        this.multaRepository = multaRepository;
        this.pagoMultaRepository = pagoMultaRepository;
    }

    @Override
    public void run(String... args) {
        // Precargar autores si la tabla está vacía
        if (autorRepository.count() == 0) {
            autorRepository.save(new Autor(null, "Gabriel García Márquez", "Colombiana", "Premio Nobel de Literatura 1982. Máximo exponente del realismo mágico."));
            autorRepository.save(new Autor(null, "Mario Vargas Llosa", "Peruana", "Premio Nobel de Literatura 2010. Miembro de la Real Academia Española."));
            autorRepository.save(new Autor(null, "Jorge Luis Borges", "Argentina", "Figura clave de la literatura en español y universal."));
            autorRepository.save(new Autor(null, "Isabel Allende", "Chilena", "Escritora viva en lengua española más leída del mundo."));
            autorRepository.save(new Autor(null, "Julio Cortázar", "Argentina", "Maestro del relato corto y la novela experimental."));
        }

        // Precargar categorías
        if (categoriaRepository.count() == 0) {
            categoriaRepository.save(new Categoria(null, "Realismo Mágico", "Obras que fusionan la realidad cotidiana con elementos fantásticos."));
            categoriaRepository.save(new Categoria(null, "Ficción Literaria", "Narrativa literaria de alta calidad estética y profundidad temática."));
            categoriaRepository.save(new Categoria(null, "Cuento y Ensayo", "Compilaciones de relatos breves y textos reflexivos."));
            categoriaRepository.save(new Categoria(null, "Novela Histórica", "Narraciones ambientadas en períodos y acontecimientos históricos reales."));
            categoriaRepository.save(new Categoria(null, "Ingeniería y Tecnología", "Textos técnicos sobre desarrollo de software y ciencias computacionales."));
        }

        // Precargar editoriales
        if (editorialRepository.count() == 0) {
            editorialRepository.save(new Editorial(null, "Editorial Sudamericana", "Argentina", "contacto@sudamericana.com"));
            editorialRepository.save(new Editorial(null, "Alfaguara", "España", "info@alfaguara.com"));
            editorialRepository.save(new Editorial(null, "Planeta", "España", "atencion@planeta.es"));
            editorialRepository.save(new Editorial(null, "Penguin Random House", "Estados Unidos", "support@penguinrandomhouse.com"));
            editorialRepository.save(new Editorial(null, "Prentice Hall", "Estados Unidos", "contact@prenticehall.com"));
        }

        // Precargar libros por ISBN
        if (libroRepository.count() == 0) {
            libroRepository.save(new Libro("978-0307474728", "Cien años de soledad", "Obra cumbre del realismo mágico latinoamericano.", 1L, 1L, 1L, 1967));
            libroRepository.save(new Libro("978-8420471839", "La ciudad y los perros", "Novela ambientada en el Colegio Militar Leoncio Prado.", 2L, 1L, 2L, 1963));
            libroRepository.save(new Libro("978-0307950925", "Ficciones", "Antología de cuentos fantásticos y filosóficos.", 3L, 1L, 3L, 1944));
            libroRepository.save(new Libro("978-0307389732", "El amor en los tiempos del cólera", "Historia de amor que perdura a través de las décadas.", 1L, 1L, 1L, 1985));
            libroRepository.save(new Libro("978-8401342653", "La casa de los espíritus", "Saga familiar de los Trueba a lo largo de cuatro generaciones.", 4L, 1L, 3L, 1982));
            libroRepository.save(new Libro("978-8420471891", "Rayuela", "Novela revolucionaria de contranovela experimental.", 5L, 1L, 2L, 1963));
        }

        // Precargar ejemplares físicos
        if (ejemplarRepository.count() == 0) {
            ejemplarRepository.save(new Ejemplar(null, "978-0307474728", 1, "Estantería A-1, Nivel 1", "EXCELENTE", "PRESTADO"));
            ejemplarRepository.save(new Ejemplar(null, "978-0307474728", 2, "Estantería A-1, Nivel 1", "BUENO", "DISPONIBLE"));
            ejemplarRepository.save(new Ejemplar(null, "978-8420471839", 1, "Estantería A-2, Nivel 2", "BUENO", "PRESTADO"));
            ejemplarRepository.save(new Ejemplar(null, "978-8420471839", 2, "Estantería A-2, Nivel 2", "REGULAR", "DISPONIBLE"));
            ejemplarRepository.save(new Ejemplar(null, "978-0307950925", 1, "Estantería B-1, Nivel 3", "EXCELENTE", "DISPONIBLE"));
            ejemplarRepository.save(new Ejemplar(null, "978-0307389732", 1, "Estantería B-2, Nivel 1", "BUENO", "DISPONIBLE"));
            ejemplarRepository.save(new Ejemplar(null, "978-8401342653", 1, "Estantería C-1, Nivel 2", "EXCELENTE", "PRESTADO"));
            ejemplarRepository.save(new Ejemplar(null, "978-8420471891", 1, "Estantería C-2, Nivel 4", "BUENO", "DISPONIBLE"));
        }

        // Precargar usuarios
        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(new Usuario(null, "72849102", "Carlos Mendoza", "carlos.mendoza@email.com", "+51 987654321", "Av. Javier Prado 1234, Lima", "ESTUDIANTE", "ACTIVO"));
            usuarioRepository.save(new Usuario(null, "45920183", "Lucía Fernández", "lucia.fernandez@email.com", "+51 912345678", "Calle Los Pinos 456, Arequipa", "DOCENTE", "ACTIVO"));
            usuarioRepository.save(new Usuario(null, "71283940", "Mateo Romero", "mateo.romero@email.com", "+51 955443322", "Jr. Huancavelica 789, Trujillo", "ESTUDIANTE", "ACTIVO"));
            usuarioRepository.save(new Usuario(null, "09283741", "Valeria Castillo", "valeria.castillo@email.com", "+51 977889900", "Av. España 321, Cusco", "DOCENTE", "ACTIVO"));
            usuarioRepository.save(new Usuario(null, "78392019", "Diego Salazar", "diego.salazar@email.com", "+51 944556677", "Calle Tacna 654, Chiclayo", "ESTUDIANTE", "ACTIVO"));
        }

        // Precargar préstamos con fechas exactas
        if (prestamoRepository.count() == 0) {
            LocalDateTime ahora = LocalDateTime.now();
            prestamoRepository.save(new Prestamo(null, 1L, 1L, ahora.minusDays(5), ahora.plusDays(9), null, "ACTIVO"));
            prestamoRepository.save(new Prestamo(null, 3L, 2L, ahora.minusDays(10), ahora.plusDays(4), null, "ACTIVO"));
            prestamoRepository.save(new Prestamo(null, 5L, 3L, ahora.minusDays(20), ahora.minusDays(6), ahora.minusDays(7), "DEVUELTO"));
            prestamoRepository.save(new Prestamo(null, 6L, 4L, ahora.minusDays(18), ahora.minusDays(10), ahora.minusDays(5), "DEVUELTO"));
            prestamoRepository.save(new Prestamo(null, 7L, 5L, ahora.minusDays(16), ahora.minusDays(2), null, "VENCIDO"));
        }

        // Precargar multas y pagos
        if (multaRepository.count() == 0) {
            LocalDateTime ahora = LocalDateTime.now();
            multaRepository.save(new Multa(null, 1L, 1L, "RETRASO", 72L, 3, 15.00, "Entrega con retraso de 3 días", ahora.minusDays(5), "PENDIENTE"));
            multaRepository.save(new Multa(null, 2L, 2L, "DAÑO", 0L, 0, 25.50, "Daño menor en cubierta de libro", ahora.minusDays(3), "PENDIENTE"));
            multaRepository.save(new Multa(null, 3L, 3L, "RETRASO", 48L, 2, 10.00, "Retraso de 2 días en devolución", ahora.minusDays(10), "PAGADA"));
            multaRepository.save(new Multa(null, 4L, 4L, "EXTRAVIO", 0L, 0, 30.00, "Extravío temporal de material", ahora.minusDays(15), "PAGADA"));
            multaRepository.save(new Multa(null, 5L, 5L, "RETRASO", 96L, 4, 20.00, "Retraso de 4 días en fecha límite", ahora.minusDays(2), "PENDIENTE"));
        }

        if (pagoMultaRepository.count() == 0) {
            pagoMultaRepository.save(new PagoMulta(null, 3L, 10.00, LocalDateTime.now().minusDays(10), "EFECTIVO", "REC-00101"));
            pagoMultaRepository.save(new PagoMulta(null, 4L, 30.00, LocalDateTime.now().minusDays(15), "TARJETA", "REC-00102"));
        }
    }
}
