package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Usuario;
import com.proyecto.biblioteca.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return usuarioRepository.findByIdAndActivoTrue(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorDni(String dni) {
        if (dni == null) return Optional.empty();
        return usuarioRepository.findByDniAndActivoTrue(dni.trim());
    }

    public Usuario guardar(Usuario usuario) {
        if (usuario.getEstado() == null || usuario.getEstado().isBlank()) {
            usuario.setEstado("ACTIVO");
        }
        if (usuario.getTipoUsuario() == null || usuario.getTipoUsuario().isBlank()) {
            usuario.setTipoUsuario("ESTUDIANTE");
        }
        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> actualizar(Long id, Usuario usuarioActualizado) {
        if (id == null) return Optional.empty();
        return usuarioRepository.findByIdAndActivoTrue(id).map(existente -> {
            existente.setDni(usuarioActualizado.getDni());
            existente.setNombre(usuarioActualizado.getNombre());
            existente.setEmail(usuarioActualizado.getEmail());
            existente.setTelefono(usuarioActualizado.getTelefono());
            existente.setDireccion(usuarioActualizado.getDireccion());
            existente.setTipoUsuario(usuarioActualizado.getTipoUsuario());
            existente.setEstado(usuarioActualizado.getEstado());
            return usuarioRepository.save(existente);
        });
    }

    public boolean eliminar(Long id) {
        if (id == null) return false;
        return usuarioRepository.findByIdAndActivoTrue(id).map(u -> {
            u.setActivo(false);
            usuarioRepository.save(u);
            return true;
        }).orElse(false);
    }

    public boolean sancionarUsuario(Long id) {
        if (id == null) return false;
        return usuarioRepository.findByIdAndActivoTrue(id).map(u -> {
            u.setEstado("SANCIONADO");
            usuarioRepository.save(u);
            return true;
        }).orElse(false);
    }

    public boolean activarUsuario(Long id) {
        if (id == null) return false;
        return usuarioRepository.findByIdAndActivoTrue(id).map(u -> {
            u.setEstado("ACTIVO");
            usuarioRepository.save(u);
            return true;
        }).orElse(false);
    }
}
