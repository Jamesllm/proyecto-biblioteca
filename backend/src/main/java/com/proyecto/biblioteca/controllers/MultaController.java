package com.proyecto.biblioteca.controllers;

import com.proyecto.biblioteca.dto.PagoMultaRequestDTO;
import com.proyecto.biblioteca.models.Multa;
import com.proyecto.biblioteca.models.PagoMulta;
import com.proyecto.biblioteca.services.MultaService;
import com.proyecto.biblioteca.services.PagoMultaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/multas")
public class MultaController {

    private final MultaService multaService;
    private final PagoMultaService pagoMultaService;

    public MultaController(MultaService multaService, PagoMultaService pagoMultaService) {
        this.multaService = multaService;
        this.pagoMultaService = pagoMultaService;
    }

    @GetMapping
    public ResponseEntity<List<Multa>> listarTodas() {
        return ResponseEntity.ok(multaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return multaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Multa>> listarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(multaService.listarPorUsuario(idUsuario));
    }

    @GetMapping("/usuario/{idUsuario}/pendientes")
    public ResponseEntity<List<Multa>> listarPendientesPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(multaService.listarPendientesPorUsuario(idUsuario));
    }

    @PostMapping
    public ResponseEntity<?> registrarMulta(@Valid @RequestBody Multa multa) {
        try {
            Multa nueva = multaService.registrarMulta(multa);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint para pagar una multa, registrar el comprobante y desbloquear al usuario
     */
    @PostMapping("/{id}/pagar")
    public ResponseEntity<?> pagarMulta(@PathVariable Long id, @Valid @RequestBody PagoMultaRequestDTO dto) {
        try {
            PagoMulta pago = pagoMultaService.procesarPago(id, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(pago);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Multa multa) {
        return multaService.actualizar(id, multa)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> anularOEliminar(@PathVariable Long id) {
        if (multaService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
