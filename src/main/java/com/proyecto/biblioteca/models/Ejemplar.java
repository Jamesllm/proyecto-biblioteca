package com.proyecto.biblioteca.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ejemplar {
    private Long id;

    @NotBlank(message = "El ISBN del libro asociado es obligatorio")
    private String isbn;

    @NotNull(message = "El número de copia es obligatorio")
    private Integer numeroCopia;

    private String ubicacion; // Ej. "Estantería A-3, Nivel 2"

    private String estadoConservacion = "BUENO"; // "EXCELENTE", "BUENO", "REGULAR", "DETERIORADO"

    private String estado = "DISPONIBLE"; // "DISPONIBLE", "PRESTADO", "EN_MANTENIMIENTO", "DADO_DE_BAJA"

    // Referencia al objeto Libro completo contenido dentro del Ejemplar
    private Libro libro;

    public boolean isDisponible() {
        return "DISPONIBLE".equalsIgnoreCase(this.estado);
    }
}
