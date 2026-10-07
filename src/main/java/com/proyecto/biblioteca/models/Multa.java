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
@Table(name = "multas")
public class Multa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del préstamo es obligatorio")
    @Column(name = "id_prestamo", nullable = false)
    private Long idPrestamo;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "tipo_multa", length = 30)
    private String tipoMulta = "RETRASO"; // "RETRASO", "DAÑO", "EXTRAVIO"

    @Column(name = "horas_retraso")
    private Long horasRetraso = 0L;

    @Column(name = "dias_retraso")
    private Integer diasRetraso = 0;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    @Column(name = "monto", nullable = false)
    private Double monto;

    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "fecha_hora_emision", nullable = false)
    private LocalDateTime fechaHoraEmision;

    @Column(name = "estado", length = 30)
    private String estado = "PENDIENTE"; // "PENDIENTE", "PAGADA", "ANULADA"

    public boolean isPagada() {
        return "PAGADA".equalsIgnoreCase(this.estado);
    }
}
