package com.proyecto.biblioteca.controllers;

import com.proyecto.biblioteca.services.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> obtenerDashboard() {
        return ResponseEntity.ok(reporteService.obtenerDashboard());
    }

    @GetMapping("/libros-por-categoria")
    public ResponseEntity<Map<String, Object>> obtenerLibrosPorCategoria() {
        return ResponseEntity.ok(reporteService.obtenerReporteLibrosPorCategoria());
    }

    @GetMapping("/prestamos")
    public ResponseEntity<Map<String, Object>> obtenerReportePrestamos() {
        return ResponseEntity.ok(reporteService.obtenerReportePrestamos());
    }
}
