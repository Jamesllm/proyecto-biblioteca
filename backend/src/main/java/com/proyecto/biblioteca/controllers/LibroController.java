package com.proyecto.biblioteca.controllers;

import com.proyecto.biblioteca.models.Ejemplar;
import com.proyecto.biblioteca.models.Libro;
import com.proyecto.biblioteca.services.EjemplarService;
import com.proyecto.biblioteca.services.LibroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService libroService;
    private final EjemplarService ejemplarService;

    public LibroController(LibroService libroService, EjemplarService ejemplarService) {
        this.libroService = libroService;
        this.ejemplarService = ejemplarService;
    }

    @GetMapping
    public ResponseEntity<List<Libro>> listarTodos() {
        return ResponseEntity.ok(libroService.listarTodos());
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<?> buscarPorIsbn(@PathVariable String isbn) {
        return libroService.buscarPorIsbn(isbn)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{isbn}/ejemplares")
    public ResponseEntity<List<Ejemplar>> listarEjemplares(@PathVariable String isbn) {
        return ResponseEntity.ok(ejemplarService.listarPorIsbn(isbn));
    }

    @GetMapping("/{isbn}/ejemplares/disponibles")
    public ResponseEntity<List<Ejemplar>> listarEjemplaresDisponibles(@PathVariable String isbn) {
        return ResponseEntity.ok(ejemplarService.listarDisponiblesPorIsbn(isbn));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Libro libro) {
        if (libroService.existe(libro.getIsbn())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe un libro registrado con el ISBN: " + libro.getIsbn());
        }
        Libro nuevo = libroService.guardar(libro);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<?> actualizar(@PathVariable String isbn, @Valid @RequestBody Libro libro) {
        return libroService.actualizar(isbn, libro)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<?> eliminar(@PathVariable String isbn) {
        if (libroService.eliminar(isbn)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
