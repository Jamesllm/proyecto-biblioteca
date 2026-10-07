package com.proyecto.biblioteca.repositories;

import com.proyecto.biblioteca.models.Multa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MultaRepository extends JpaRepository<Multa, Long> {
    List<Multa> findByIdUsuario(Long idUsuario);
    List<Multa> findByIdUsuarioAndEstadoIgnoreCase(Long idUsuario, String estado);
    Optional<Multa> findByIdPrestamo(Long idPrestamo);
    List<Multa> findByEstadoIgnoreCase(String estado);
}
