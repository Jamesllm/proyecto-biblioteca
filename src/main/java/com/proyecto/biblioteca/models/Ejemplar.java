package com.proyecto.biblioteca.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ejemplares")
public class Ejemplar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El ISBN del libro asociado es obligatorio")
    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn;

    @NotNull(message = "El número de copia es obligatorio")
    @Column(name = "numero_copia", nullable = false)
    private Integer numeroCopia;

    @Column(name = "ubicacion")
    private String ubicacion;

    @Column(name = "estado_conservacion", length = 30)
    private String estadoConservacion = "BUENO";

    @Column(name = "estado", length = 30)
    private String estado = "DISPONIBLE";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "isbn", referencedColumnName = "isbn", insertable = false, updatable = false)
    private Libro libro;

    public Ejemplar(Long id, String isbn, Integer numeroCopia, String ubicacion, String estadoConservacion, String estado) {
        this.id = id;
        this.isbn = isbn;
        this.numeroCopia = numeroCopia;
        this.ubicacion = ubicacion;
        this.estadoConservacion = estadoConservacion != null ? estadoConservacion : "BUENO";
        this.estado = estado != null ? estado : "DISPONIBLE";
    }

    public boolean isDisponible() {
        return "DISPONIBLE".equalsIgnoreCase(this.estado);
    }
}
