package com.proyecto.biblioteca.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @NotBlank(message = "El código ISBN es obligatorio y actúa como identificador único")
    @Column(name = "isbn", length = 20, nullable = false, unique = true)
    private String isbn;

    @NotBlank(message = "El título del libro es obligatorio")
    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "sinopsis", columnDefinition = "TEXT")
    private String sinopsis;

    @NotNull(message = "El ID del autor es obligatorio")
    @Column(name = "id_autor", nullable = false)
    private Long idAutor;

    @NotNull(message = "El ID de la categoría es obligatorio")
    @Column(name = "id_categoria", nullable = false)
    private Long idCategoria;

    @NotNull(message = "El ID de la editorial es obligatorio")
    @Column(name = "id_editorial", nullable = false)
    private Long idEditorial;

    @NotNull(message = "El año de publicación es obligatorio")
    @Min(value = 1000, message = "El año de publicación debe ser válido")
    @Column(name = "anio_publicacion", nullable = false)
    private Integer anioPublicacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_autor", insertable = false, updatable = false)
    private Autor autor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", insertable = false, updatable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_editorial", insertable = false, updatable = false)
    private Editorial editorial;

    public Libro(String isbn, String titulo, String sinopsis, Long idAutor, Long idCategoria, Long idEditorial, Integer anioPublicacion) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.sinopsis = sinopsis;
        this.idAutor = idAutor;
        this.idCategoria = idCategoria;
        this.idEditorial = idEditorial;
        this.anioPublicacion = anioPublicacion;
    }
}
