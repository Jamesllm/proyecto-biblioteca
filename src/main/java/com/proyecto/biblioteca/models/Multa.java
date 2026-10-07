package com.proyecto.biblioteca.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Multa {
    private Long id;

    @NotNull(message = "El ID del préstamo es obligatorio")
    private Long idPrestamo;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    private String tipoMulta = "RETRASO"; // "RETRASO", "DAÑO", "EXTRAVIO"

    private Long horasRetraso = 0L;
    private Integer diasRetraso = 0;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private Double monto;

    private String motivo;

    private LocalDateTime fechaHoraEmision;

    private String estado = "PENDIENTE"; // "PENDIENTE", "PAGADA", "ANULADA"

    public boolean isPagada() {
        return "PAGADA".equalsIgnoreCase(this.estado);
    }
}
