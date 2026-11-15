package com.proyecto.biblioteca.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "editoriales")
public class Editorial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la editorial es obligatorio")
    @Column(nullable = false, unique = true)
    private String nombre;

    private String pais;

    private String contacto;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    public Editorial(Long id, String nombre, String pais, String contacto) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
        this.contacto = contacto;
        this.activo = true;
    }
}
