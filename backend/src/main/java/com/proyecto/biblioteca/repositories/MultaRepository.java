package com.proyecto.biblioteca.repositories;

import com.proyecto.biblioteca.models.Multa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MultaRepository extends JpaRepository<Multa, Long> {
    List<Multa> findByActivoTrue();
    Optional<Multa> findByIdAndActivoTrue(Long id);
    List<Multa> findByIdUsuario(Long idUsuario);
    List<Multa> findByIdUsuarioAndActivoTrue(Long idUsuario);
    List<Multa> findByIdUsuarioAndEstadoIgnoreCase(Long idUsuario, String estado);
    List<Multa> findByIdUsuarioAndEstadoIgnoreCaseAndActivoTrue(Long idUsuario, String estado);
    Optional<Multa> findByIdPrestamo(Long idPrestamo);
    Optional<Multa> findByIdPrestamoAndActivoTrue(Long idPrestamo);
    List<Multa> findByEstadoIgnoreCase(String estado);
    List<Multa> findByEstadoIgnoreCaseAndActivoTrue(String estado);
}
