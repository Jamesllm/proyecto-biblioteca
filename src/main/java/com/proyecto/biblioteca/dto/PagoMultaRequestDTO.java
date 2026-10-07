package com.proyecto.biblioteca.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoMultaRequestDTO {
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private Double montoPagado;

    private String metodoPago = "EFECTIVO"; // EFECTIVO, TARJETA, TRANSFERENCIA, YAPE_PLIN
    private String comprobante;
}
