package com.proyecto.biblioteca.repositories;

import com.proyecto.biblioteca.models.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, String> {
    List<Libro> findByActivoTrue();
    Optional<Libro> findByIsbnAndActivoTrue(String isbn);
    List<Libro> findByIdAutor(Long idAutor);
    List<Libro> findByIdAutorAndActivoTrue(Long idAutor);
    List<Libro> findByIdCategoria(Long idCategoria);
    List<Libro> findByIdCategoriaAndActivoTrue(Long idCategoria);
    List<Libro> findByIdEditorial(Long idEditorial);
    List<Libro> findByIdEditorialAndActivoTrue(Long idEditorial);
    List<Libro> findByTituloContainingIgnoreCase(String titulo);
    List<Libro> findByTituloContainingIgnoreCaseAndActivoTrue(String titulo);
}
