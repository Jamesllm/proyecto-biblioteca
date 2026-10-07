package com.proyecto.biblioteca.repositories;

import com.proyecto.biblioteca.models.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, String> {
    List<Libro> findByIdAutor(Long idAutor);
    List<Libro> findByIdCategoria(Long idCategoria);
    List<Libro> findByIdEditorial(Long idEditorial);
    List<Libro> findByTituloContainingIgnoreCase(String titulo);
}
