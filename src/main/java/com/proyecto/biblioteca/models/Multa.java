package com.proyecto.biblioteca.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Multa {
    private Long id;

    @NotNull(message = "El ID del préstamo es obligatorio")
    private Long idPrestamo;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private Double monto;

    private String motivo;

    private Boolean pagada = false;

    public boolean isPagada() {
        return Boolean.TRUE.equals(pagada);
    }
}
