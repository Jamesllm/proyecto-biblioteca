package com.proyecto.biblioteca.repositories;

import com.proyecto.biblioteca.models.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    List<Prestamo> findByIdUsuario(Long idUsuario);
    List<Prestamo> findByIdUsuarioAndEstadoIn(Long idUsuario, List<String> estados);
    List<Prestamo> findByEstadoIgnoreCase(String estado);
}
