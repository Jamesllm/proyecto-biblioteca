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
public class PagoMulta {
    private Long id;

    @NotNull(message = "El ID de la multa es obligatorio")
    private Long idMulta;

    @NotNull(message = "El monto pagado es obligatorio")
    @Positive(message = "El monto pagado debe ser mayor a cero")
    private Double montoPagado;

    private LocalDateTime fechaHoraPago;

    private String metodoPago = "EFECTIVO"; // "EFECTIVO", "TARJETA", "TRANSFERENCIA", "YAPE_PLIN"

    private String comprobante; // Ej: "REC-000105"
}
