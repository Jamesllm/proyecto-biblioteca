package com.proyecto.biblioteca.repositories;

import com.proyecto.biblioteca.models.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
    List<Autor> findByActivoTrue();
    Optional<Autor> findByIdAndActivoTrue(Long id);
}
