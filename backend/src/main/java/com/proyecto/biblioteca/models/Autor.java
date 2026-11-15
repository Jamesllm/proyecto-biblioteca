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
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del autor es obligatorio")
    @Column(nullable = false)
    private String nombre;

    private String nacionalidad;

    @Column(columnDefinition = "TEXT")
    private String biografia;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    public Autor(Long id, String nombre, String nacionalidad, String biografia) {
        this.id = id;
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
        this.biografia = biografia;
        this.activo = true;
    }
}
