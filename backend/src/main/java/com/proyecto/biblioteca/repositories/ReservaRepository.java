package com.proyecto.biblioteca.repositories;

import com.proyecto.biblioteca.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByActivoTrue();
    Optional<Reserva> findByIdAndActivoTrue(Long id);
    List<Reserva> findByIdUsuario(Long idUsuario);
    List<Reserva> findByIdUsuarioAndActivoTrue(Long idUsuario);
    List<Reserva> findByIsbn(String isbn);
    List<Reserva> findByIsbnAndActivoTrue(String isbn);
    List<Reserva> findByEstadoIgnoreCase(String estado);
    List<Reserva> findByEstadoIgnoreCaseAndActivoTrue(String estado);
}
