package com.proyecto.biblioteca.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pagos_multas")
public class PagoMulta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID de la multa es obligatorio")
    @Column(name = "id_multa", nullable = false)
    private Long idMulta;

    @NotNull(message = "El monto pagado es obligatorio")
    @Positive(message = "El monto pagado debe ser mayor a cero")
    @Column(name = "monto_pagado", nullable = false)
    private Double montoPagado;

    @Column(name = "fecha_hora_pago", nullable = false)
    private LocalDateTime fechaHoraPago;

    @Column(name = "metodo_pago", length = 30)
    private String metodoPago = "EFECTIVO"; // "EFECTIVO", "TARJETA", "TRANSFERENCIA", "YAPE_PLIN"

    @Column(name = "comprobante", length = 50)
    private String comprobante;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    public PagoMulta(Long id, Long idMulta, Double montoPagado, LocalDateTime fechaHoraPago, String metodoPago, String comprobante) {
        this.id = id;
        this.idMulta = idMulta;
        this.montoPagado = montoPagado;
        this.fechaHoraPago = fechaHoraPago;
        this.metodoPago = metodoPago != null ? metodoPago : "EFECTIVO";
        this.comprobante = comprobante;
        this.activo = true;
    }
}
