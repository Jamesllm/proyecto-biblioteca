package com.proyecto.biblioteca.models;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "tipo_usuario", length = 30)
    private String tipoUsuario = "ESTUDIANTE"; // "ESTUDIANTE", "DOCENTE", "INVESTIGADOR", "EXTERNO"

    @Column(name = "rol", length = 30)
    private String rol = "ROLE_ESTUDIANTE"; // "ROLE_ADMIN", "ROLE_BIBLIOTECARIO", "ROLE_ESTUDIANTE", "ROLE_DOCENTE"

    @Column(name = "estado", length = 30)
    private String estado = "ACTIVO"; // "ACTIVO", "SANCIONADO", "INACTIVO"

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    public Usuario(Long id, String dni, String nombre, String email, String password, String telefono,
                   String direccion, String tipoUsuario, String rol, String estado) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.direccion = direccion;
        this.tipoUsuario = tipoUsuario != null ? tipoUsuario : "ESTUDIANTE";
        this.rol = rol != null ? rol : "ROLE_ESTUDIANTE";
        this.estado = estado != null ? estado : "ACTIVO";
        this.activo = true;
    }

    public boolean isActivo() {
        return Boolean.TRUE.equals(this.activo) && "ACTIVO".equalsIgnoreCase(this.estado);
    }
}
