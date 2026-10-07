package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Usuario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UsuarioService {
    private final ConcurrentHashMap<Long, Usuario> usuarios = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public UsuarioService() {
        // Datos iniciales de usuarios
        guardar(new Usuario(null, "72849102", "Carlos Mendoza", "carlos.mendoza@email.com", "+51 987654321", "Av. Javier Prado 1234, Lima", "ESTUDIANTE", "ACTIVO"));
        guardar(new Usuario(null, "45920183", "Lucía Fernández", "lucia.fernandez@email.com", "+51 912345678", "Calle Los Pinos 456, Arequipa", "DOCENTE", "ACTIVO"));
        guardar(new Usuario(null, "71283940", "Mateo Romero", "mateo.romero@email.com", "+51 955443322", "Jr. Huancavelica 789, Trujillo", "ESTUDIANTE", "ACTIVO"));
        guardar(new Usuario(null, "09283741", "Valeria Castillo", "valeria.castillo@email.com", "+51 977889900", "Av. España 321, Cusco", "DOCENTE", "ACTIVO"));
        guardar(new Usuario(null, "78392019", "Diego Salazar", "diego.salazar@email.com", "+51 944556677", "Calle Tacna 654, Chiclayo", "ESTUDIANTE", "ACTIVO"));
    }

    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios.values());
    }

    public Optional<Usuario> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(usuarios.get(id));
    }

    public Usuario guardar(Usuario usuario) {
        if (usuario.getId() == null || usuario.getId() <= 0) {
            usuario.setId(idGenerator.incrementAndGet());
        } else {
            idGenerator.updateAndGet(current -> Math.max(current, usuario.getId()));
        }
        if (usuario.getEstado() == null || usuario.getEstado().isBlank()) {
            usuario.setEstado("ACTIVO");
        }
        if (usuario.getTipoUsuario() == null || usuario.getTipoUsuario().isBlank()) {
            usuario.setTipoUsuario("ESTUDIANTE");
        }
        usuarios.put(usuario.getId(), usuario);
        return usuario;
    }

    public Optional<Usuario> actualizar(Long id, Usuario usuarioActualizado) {
        if (id == null || !usuarios.containsKey(id)) {
            return Optional.empty();
        }
        usuarioActualizado.setId(id);
        usuarios.put(id, usuarioActualizado);
        return Optional.of(usuarioActualizado);
    }

    public boolean eliminar(Long id) {
        if (id == null) return false;
        return usuarios.remove(id) != null;
    }

    public boolean sancionarUsuario(Long id) {
        Usuario usuario = usuarios.get(id);
        if (usuario != null) {
            usuario.setEstado("SANCIONADO");
            return true;
        }
        return false;
    }

    public boolean activarUsuario(Long id) {
        Usuario usuario = usuarios.get(id);
        if (usuario != null) {
            usuario.setEstado("ACTIVO");
            return true;
        }
        return false;
    }
}
