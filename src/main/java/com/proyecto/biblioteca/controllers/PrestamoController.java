package com.proyecto.biblioteca.controllers;

import com.proyecto.biblioteca.dto.DevolucionRequestDTO;
import com.proyecto.biblioteca.models.Prestamo;
import com.proyecto.biblioteca.services.PrestamoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping
    public ResponseEntity<List<Prestamo>> listarTodos() {
        return ResponseEntity.ok(prestamoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return prestamoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{id}/detalle")
    public ResponseEntity<?> obtenerDetalle(@PathVariable Long id) {
        return prestamoService.obtenerDetallePrestamo(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Prestamo>> listarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(prestamoService.listarPorUsuario(idUsuario));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Prestamo prestamo) {
        try {
            Prestamo nuevo = prestamoService.registrarPrestamo(prestamo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Endpoint para procesar la devolución de un ejemplar físico, calculando horas/días de retraso y multas automáticas
     */
    @PutMapping("/{id}/devolver")
    public ResponseEntity<?> registrarDevolucionPut(@PathVariable Long id, @RequestBody(required = false) DevolucionRequestDTO dto) {
        try {
            Prestamo devuelto = prestamoService.registrarDevolucion(id, dto);
            return ResponseEntity.ok(devuelto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/devolver")
    public ResponseEntity<?> registrarDevolucionPost(@PathVariable Long id, @RequestBody(required = false) DevolucionRequestDTO dto) {
        return registrarDevolucionPut(id, dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Prestamo prestamo) {
        return prestamoService.actualizar(id, prestamo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (prestamoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
