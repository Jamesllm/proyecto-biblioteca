package com.proyecto.biblioteca.controllers;

import com.proyecto.biblioteca.dto.PagoMultaRequestDTO;
import com.proyecto.biblioteca.models.PagoMulta;
import com.proyecto.biblioteca.services.PagoMultaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos-multas")
public class PagoMultaController {

    private final PagoMultaService pagoMultaService;

    public PagoMultaController(PagoMultaService pagoMultaService) {
        this.pagoMultaService = pagoMultaService;
    }

    @GetMapping
    public ResponseEntity<List<PagoMulta>> listarTodos() {
        return ResponseEntity.ok(pagoMultaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return pagoMultaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/multa/{idMulta}")
    public ResponseEntity<List<PagoMulta>> listarPorMulta(@PathVariable Long idMulta) {
        return ResponseEntity.ok(pagoMultaService.listarPorMulta(idMulta));
    }

    @PostMapping("/multa/{idMulta}")
    public ResponseEntity<?> procesarPago(@PathVariable Long idMulta, @Valid @RequestBody PagoMultaRequestDTO dto) {
        try {
            PagoMulta pago = pagoMultaService.procesarPago(idMulta, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(pago);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
