package com.proyecto.biblioteca.repositories;

import com.proyecto.biblioteca.models.Editorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EditorialRepository extends JpaRepository<Editorial, Long> {
    List<Editorial> findByActivoTrue();
    Optional<Editorial> findByIdAndActivoTrue(Long id);
}
