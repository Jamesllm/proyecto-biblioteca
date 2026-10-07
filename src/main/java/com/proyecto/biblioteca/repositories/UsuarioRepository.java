package com.proyecto.biblioteca.repositories;

import com.proyecto.biblioteca.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    List<Usuario> findByActivoTrue();
    Optional<Usuario> findByIdAndActivoTrue(Long id);
    Optional<Usuario> findByDni(String dni);
    Optional<Usuario> findByDniAndActivoTrue(String dni);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailAndActivoTrue(String email);
}
