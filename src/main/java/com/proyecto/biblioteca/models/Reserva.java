package com.proyecto.biblioteca.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código ISBN del libro es obligatorio")
    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "fecha_hora_reserva", nullable = false)
    private LocalDateTime fechaHoraReserva;

    @Column(name = "fecha_hora_expiracion")
    private LocalDateTime fechaHoraExpiracion;

    @Column(name = "estado", length = 30)
    private String estado = "PENDIENTE"; // "PENDIENTE", "ATENDIDA", "CANCELADA", "EXPIRADA"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "isbn", referencedColumnName = "isbn", insertable = false, updatable = false)
    private Libro libro;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    private Usuario usuario;

    public Reserva(Long id, String isbn, Long idUsuario, LocalDateTime fechaHoraReserva,
                   LocalDateTime fechaHoraExpiracion, String estado) {
        this.id = id;
        this.isbn = isbn;
        this.idUsuario = idUsuario;
        this.fechaHoraReserva = fechaHoraReserva;
        this.fechaHoraExpiracion = fechaHoraExpiracion;
        this.estado = estado != null ? estado : "PENDIENTE";
    }
}
