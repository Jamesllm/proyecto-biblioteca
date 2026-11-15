package com.proyecto.biblioteca.repositories;

import com.proyecto.biblioteca.models.PagoMulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoMultaRepository extends JpaRepository<PagoMulta, Long> {
    List<PagoMulta> findByActivoTrue();
    Optional<PagoMulta> findByIdAndActivoTrue(Long id);
    List<PagoMulta> findByIdMulta(Long idMulta);
    List<PagoMulta> findByIdMultaAndActivoTrue(Long idMulta);
}
