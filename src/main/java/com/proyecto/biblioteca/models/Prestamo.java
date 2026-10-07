package com.proyecto.biblioteca.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prestamo {
    private Long id;

    @NotNull(message = "El ID del libro es obligatorio")
    private Long idLibro;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private String estado; // ej: "ACTIVO", "DEVUELTO", "VENCIDO"
}
