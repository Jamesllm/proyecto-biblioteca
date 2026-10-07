package com.proyecto.biblioteca.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Libro {
    @NotBlank(message = "El código ISBN es obligatorio y actúa como identificador único")
    private String isbn;

    @NotBlank(message = "El título del libro es obligatorio")
    private String titulo;

    private String sinopsis;

    @NotNull(message = "El ID del autor es obligatorio")
    private Long idAutor;

    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long idCategoria;

    @NotNull(message = "El ID de la editorial es obligatorio")
    private Long idEditorial;

    @NotNull(message = "El año de publicación es obligatorio")
    @Min(value = 1000, message = "El año de publicación debe ser válido")
    private Integer anioPublicacion;
}
