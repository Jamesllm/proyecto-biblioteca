package com.proyecto.biblioteca.repositories;

import com.proyecto.biblioteca.models.Ejemplar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EjemplarRepository extends JpaRepository<Ejemplar, Long> {
    List<Ejemplar> findByActivoTrue();
    Optional<Ejemplar> findByIdAndActivoTrue(Long id);
    List<Ejemplar> findByIsbn(String isbn);
    List<Ejemplar> findByIsbnAndActivoTrue(String isbn);
    List<Ejemplar> findByIsbnAndEstadoIgnoreCase(String isbn, String estado);
    List<Ejemplar> findByIsbnAndEstadoIgnoreCaseAndActivoTrue(String isbn, String estado);
    List<Ejemplar> findByEstadoIgnoreCase(String estado);
    List<Ejemplar> findByEstadoIgnoreCaseAndActivoTrue(String estado);
}
