package com.proyecto.biblioteca.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
    private Long id;

    @NotBlank(message = "El código ISBN del libro es obligatorio")
    private String isbn;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    private LocalDateTime fechaHoraReserva;
    private LocalDateTime fechaHoraExpiracion;
    private String estado = "PENDIENTE"; // "PENDIENTE", "ATENDIDA", "CANCELADA", "EXPIRADA"

    private Libro libro;
    private Usuario usuario;
}
