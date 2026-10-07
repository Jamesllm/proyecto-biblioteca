package com.proyecto.biblioteca.repositories;

import com.proyecto.biblioteca.models.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    List<Prestamo> findByActivoTrue();
    Optional<Prestamo> findByIdAndActivoTrue(Long id);
    List<Prestamo> findByIdUsuario(Long idUsuario);
    List<Prestamo> findByIdUsuarioAndActivoTrue(Long idUsuario);
    List<Prestamo> findByIdUsuarioAndEstadoIn(Long idUsuario, List<String> estados);
    List<Prestamo> findByIdUsuarioAndEstadoInAndActivoTrue(Long idUsuario, List<String> estados);
    List<Prestamo> findByEstadoIgnoreCase(String estado);
    List<Prestamo> findByEstadoIgnoreCaseAndActivoTrue(String estado);
}
