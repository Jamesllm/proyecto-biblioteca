package com.proyecto.biblioteca.repositories;

import com.proyecto.biblioteca.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByIdUsuario(Long idUsuario);
    List<Reserva> findByIsbn(String isbn);
    List<Reserva> findByEstadoIgnoreCase(String estado);
}
