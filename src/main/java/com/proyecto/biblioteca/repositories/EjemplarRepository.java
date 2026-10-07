package com.proyecto.biblioteca.repositories;

import com.proyecto.biblioteca.models.Ejemplar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjemplarRepository extends JpaRepository<Ejemplar, Long> {
    List<Ejemplar> findByIsbn(String isbn);
    List<Ejemplar> findByIsbnAndEstadoIgnoreCase(String isbn, String estado);
    List<Ejemplar> findByEstadoIgnoreCase(String estado);
}
