package com.proyecto.biblioteca.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prestamos")
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del ejemplar físico es obligatorio")
    @Column(name = "id_ejemplar", nullable = false)
    private Long idEjemplar;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "fecha_hora_prestamo", nullable = false)
    private LocalDateTime fechaHoraPrestamo;

    @Column(name = "fecha_hora_devolucion_esperada", nullable = false)
    private LocalDateTime fechaHoraDevolucionEsperada;

    @Column(name = "fecha_hora_devolucion_real")
    private LocalDateTime fechaHoraDevolucionReal;

    @Column(name = "estado", length = 30)
    private String estado = "ACTIVO"; // "ACTIVO", "DEVUELTO", "VENCIDO"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_ejemplar", insertable = false, updatable = false)
    private Ejemplar ejemplar;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    private Usuario usuario;

    public Prestamo(Long id, Long idEjemplar, Long idUsuario, LocalDateTime fechaHoraPrestamo,
                    LocalDateTime fechaHoraDevolucionEsperada, LocalDateTime fechaHoraDevolucionReal, String estado) {
        this.id = id;
        this.idEjemplar = idEjemplar;
        this.idUsuario = idUsuario;
        this.fechaHoraPrestamo = fechaHoraPrestamo;
        this.fechaHoraDevolucionEsperada = fechaHoraDevolucionEsperada;
        this.fechaHoraDevolucionReal = fechaHoraDevolucionReal;
        this.estado = estado != null ? estado : "ACTIVO";
    }
}
