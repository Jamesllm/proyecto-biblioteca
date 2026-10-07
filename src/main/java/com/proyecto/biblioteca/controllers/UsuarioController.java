package com.proyecto.biblioteca.controllers;

import com.proyecto.biblioteca.dto.PrestamoDetalladoDTO;
import com.proyecto.biblioteca.models.Multa;
import com.proyecto.biblioteca.models.Usuario;
import com.proyecto.biblioteca.services.MultaService;
import com.proyecto.biblioteca.services.PrestamoService;
import com.proyecto.biblioteca.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PrestamoService prestamoService;
    private final MultaService multaService;

    public UsuarioController(UsuarioService usuarioService,
                             PrestamoService prestamoService,
                             MultaService multaService) {
        this.usuarioService = usuarioService;
        this.prestamoService = prestamoService;
        this.multaService = multaService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@Valid @RequestBody Usuario usuario) {
        Usuario nuevo = usuarioService.guardar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        return usuarioService.actualizar(id, usuario)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (usuarioService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Consulta detallada del historial completo de préstamos de un usuario
     */
    @GetMapping("/{id}/prestamos")
    public ResponseEntity<?> listarPrestamosDeUsuario(@PathVariable Long id) {
        if (usuarioService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado con ID: " + id);
        }
        List<PrestamoDetalladoDTO> prestamos = prestamoService.obtenerDetallePrestamosPorUsuario(id, false);
        return ResponseEntity.ok(prestamos);
    }

    /**
     * Consulta detallada de los libros que el usuario tiene prestados actualmente en su poder
     */
    @GetMapping("/{id}/prestamos/activos")
    public ResponseEntity<?> listarPrestamosActivosDeUsuario(@PathVariable Long id) {
        if (usuarioService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado con ID: " + id);
        }
        List<PrestamoDetalladoDTO> prestamosActivos = prestamoService.obtenerDetallePrestamosPorUsuario(id, true);
        return ResponseEntity.ok(prestamosActivos);
    }

    /**
     * Listado de multas asociadas al usuario
     */
    @GetMapping("/{id}/multas")
    public ResponseEntity<?> listarMultasDeUsuario(@PathVariable Long id) {
        if (usuarioService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado con ID: " + id);
        }
        List<Multa> multas = multaService.listarPorUsuario(id);
        return ResponseEntity.ok(multas);
    }
}
