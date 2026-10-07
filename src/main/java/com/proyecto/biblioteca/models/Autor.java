package com.proyecto.biblioteca.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Autor {
    private Long id;

    @NotBlank(message = "El nombre del autor es obligatorio")
    private String nombre;

    private String nacionalidad;
    private String biografia;
}
