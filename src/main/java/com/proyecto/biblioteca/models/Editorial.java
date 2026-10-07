package com.proyecto.biblioteca.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Editorial {
    private Long id;

    @NotBlank(message = "El nombre de la editorial es obligatorio")
    private String nombre;

    private String pais;
    private String sitioWeb;
}
