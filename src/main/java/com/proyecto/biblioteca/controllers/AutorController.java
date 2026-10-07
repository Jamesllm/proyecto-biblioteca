package com.proyecto.biblioteca.controllers;

import com.proyecto.biblioteca.models.Autor;
import com.proyecto.biblioteca.services.AutorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/autores")
public class AutorController {

    private final AutorService autorService;

    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    @GetMapping
    public List<Autor> listar() {
        return autorService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Autor> obtenerPorId(@PathVariable Long id) {
        return autorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Autor> crear(@Valid @RequestBody Autor autor) {
        Autor nuevoAutor = autorService.guardar(autor);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAutor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Autor> actualizar(@PathVariable Long id, @Valid @RequestBody Autor autor) {
        return autorService.actualizar(id, autor)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
