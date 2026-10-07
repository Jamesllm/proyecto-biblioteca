package com.proyecto.biblioteca.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private Long id;

    @NotBlank(message = "El nombre del usuario es obligatorio")
    private String nombre;

    @NotBlank(message = "El email del usuario es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    private String telefono;
    private String direccion;
}
