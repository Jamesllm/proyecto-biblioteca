package com.proyecto.biblioteca.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prestamo {
    private Long id;

    @NotNull(message = "El ID del ejemplar físico es obligatorio")
    private Long idEjemplar;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    private LocalDateTime fechaHoraPrestamo;
    private LocalDateTime fechaHoraDevolucionEsperada;
    private LocalDateTime fechaHoraDevolucionReal;
    private String estado = "ACTIVO"; // "ACTIVO", "DEVUELTO", "VENCIDO"

    // Objetos cargados para respuestas detalladas
    private Ejemplar ejemplar;
    private Usuario usuario;
}
