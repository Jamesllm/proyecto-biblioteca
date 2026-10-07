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
        // Datos iniciales de prueba (mínimo 5 registros)
        guardar(new Usuario(null, "Carlos Mendoza", "carlos.mendoza@email.com", "+51 987654321", "Av. Javier Prado 1234, Lima"));
        guardar(new Usuario(null, "Lucía Fernández", "lucia.fernandez@email.com", "+51 912345678", "Calle Los Pinos 456, Arequipa"));
        guardar(new Usuario(null, "Mateo Romero", "mateo.romero@email.com", "+51 955443322", "Jr. Huancavelica 789, Trujillo"));
        guardar(new Usuario(null, "Valeria Castillo", "valeria.castillo@email.com", "+51 977889900", "Av. España 321, Cusco"));
        guardar(new Usuario(null, "Diego Salazar", "diego.salazar@email.com", "+51 944556677", "Calle Tacna 654, Chiclayo"));
    }

    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios.values());
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return Optional.ofNullable(usuarios.get(id));
    }

    public Usuario guardar(Usuario usuario) {
        if (usuario.getId() == null || usuario.getId() <= 0) {
            usuario.setId(idGenerator.incrementAndGet());
        } else {
            idGenerator.updateAndGet(current -> Math.max(current, usuario.getId()));
        }
        usuarios.put(usuario.getId(), usuario);
        return usuario;
    }

    public Optional<Usuario> actualizar(Long id, Usuario usuarioActualizado) {
        if (!usuarios.containsKey(id)) {
            return Optional.empty();
        }
        usuarioActualizado.setId(id);
        usuarios.put(id, usuarioActualizado);
        return Optional.of(usuarioActualizado);
    }
}
