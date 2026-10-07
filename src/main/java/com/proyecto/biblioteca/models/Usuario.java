package com.proyecto.biblioteca.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dni", length = 15, unique = true)
    private String dni;

    @NotBlank(message = "El nombre del usuario es obligatorio")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotBlank(message = "El email del usuario es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "tipo_usuario", length = 30)
    private String tipoUsuario = "ESTUDIANTE";

    @Column(name = "estado", length = 30)
    private String estado = "ACTIVO";

    public boolean isActivo() {
        return "ACTIVO".equalsIgnoreCase(this.estado);
    }
}
