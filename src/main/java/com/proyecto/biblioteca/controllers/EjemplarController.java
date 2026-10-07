package com.proyecto.biblioteca.controllers;

import com.proyecto.biblioteca.models.Ejemplar;
import com.proyecto.biblioteca.services.EjemplarService;
import com.proyecto.biblioteca.services.LibroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ejemplares")
public class EjemplarController {

    private final EjemplarService ejemplarService;
    private final LibroService libroService;

    public EjemplarController(EjemplarService ejemplarService, LibroService libroService) {
        this.ejemplarService = ejemplarService;
        this.libroService = libroService;
    }

    @GetMapping
    public ResponseEntity<List<Ejemplar>> listarTodos() {
        return ResponseEntity.ok(ejemplarService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return ejemplarService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/libro/{isbn}")
    public ResponseEntity<List<Ejemplar>> listarPorIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(ejemplarService.listarPorIsbn(isbn));
    }

    @GetMapping("/libro/{isbn}/disponibles")
    public ResponseEntity<List<Ejemplar>> listarDisponiblesPorIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(ejemplarService.listarDisponiblesPorIsbn(isbn));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Ejemplar ejemplar) {
        if (!libroService.existe(ejemplar.getIsbn())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No se puede registrar un ejemplar para un ISBN no existente: " + ejemplar.getIsbn());
        }
        Ejemplar nuevo = ejemplarService.guardar(ejemplar);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Ejemplar ejemplar) {
        return ejemplarService.actualizar(id, ejemplar)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (ejemplarService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
