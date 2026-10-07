package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Editorial;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EditorialService {
    private final ConcurrentHashMap<Long, Editorial> editoriales = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public EditorialService() {
        // Datos iniciales de prueba (mínimo 5 registros)
        guardar(new Editorial(null, "Editorial Sudamericana", "Argentina", "https://www.penguinlibros.com/ar"));
        guardar(new Editorial(null, "Alfaguara", "España", "https://www.penguinlibros.com/es/alfaguara"));
        guardar(new Editorial(null, "Grupo Editorial Planeta", "España", "https://www.planetadelibros.com"));
        guardar(new Editorial(null, "Fondo de Cultura Económica", "México", "https://www.fondodeculturaeconomica.com"));
        guardar(new Editorial(null, "Anagrama", "España", "https://www.anagrama-ed.es"));
    }

    public List<Editorial> listarTodas() {
        return new ArrayList<>(editoriales.values());
    }

    public Optional<Editorial> buscarPorId(Long id) {
        return Optional.ofNullable(editoriales.get(id));
    }

    public Editorial guardar(Editorial editorial) {
        if (editorial.getId() == null || editorial.getId() <= 0) {
            editorial.setId(idGenerator.incrementAndGet());
        } else {
            idGenerator.updateAndGet(current -> Math.max(current, editorial.getId()));
        }
        editoriales.put(editorial.getId(), editorial);
        return editorial;
    }

    public Optional<Editorial> actualizar(Long id, Editorial editorialActualizada) {
        if (!editoriales.containsKey(id)) {
            return Optional.empty();
        }
        editorialActualizada.setId(id);
        editoriales.put(id, editorialActualizada);
        return Optional.of(editorialActualizada);
    }
}
