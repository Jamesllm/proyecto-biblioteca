package com.proyecto.biblioteca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    private String dni;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener mínimo 6 caracteres")
    private String password;

    private String telefono;
    private String direccion;
    private String tipoUsuario = "ESTUDIANTE"; // ESTUDIANTE, DOCENTE, EXTERNO
    private String rol = "ROLE_ESTUDIANTE";     // ROLE_ESTUDIANTE, ROLE_DOCENTE, ROLE_BIBLIOTECARIO, ROLE_ADMIN
}
