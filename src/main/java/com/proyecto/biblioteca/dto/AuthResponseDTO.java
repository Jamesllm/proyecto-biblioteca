package com.proyecto.biblioteca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String tipo; // "Bearer"
    private Long id;
    private String dni;
    private String nombre;
    private String email;
    private String rol;
    private String tipoUsuario;
    private String estado;
}
