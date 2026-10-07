package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Autor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AutorService {
    private final ConcurrentHashMap<Long, Autor> autores = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public AutorService() {
        // Datos iniciales de prueba (mínimo 5 registros)
        guardar(new Autor(null, "Gabriel García Márquez", "Colombiana", "Premio Nobel de Literatura 1982, maestro del realismo mágico."));
        guardar(new Autor(null, "Mario Vargas Llosa", "Peruana", "Premio Nobel de Literatura 2010, novelista y ensayista."));
        guardar(new Autor(null, "Jorge Luis Borges", "Argentina", "Figura clave de la literatura en habla hispana y universal."));
        guardar(new Autor(null, "Isabel Allende", "Chilena", "Destacada escritora latinoamericana, autora de 'La casa de los espíritus'."));
        guardar(new Autor(null, "Julio Cortázar", "Argentina", "Maestro del relato corto, la prosa poética y la narración experimental, autor de 'Rayuela'."));
    }

    public List<Autor> listarTodos() {
        return new ArrayList<>(autores.values());
    }

    public Optional<Autor> buscarPorId(Long id) {
        return Optional.ofNullable(autores.get(id));
    }

    public Autor guardar(Autor autor) {
        if (autor.getId() == null || autor.getId() <= 0) {
            autor.setId(idGenerator.incrementAndGet());
        } else {
            idGenerator.updateAndGet(current -> Math.max(current, autor.getId()));
        }
        autores.put(autor.getId(), autor);
        return autor;
    }

    public Optional<Autor> actualizar(Long id, Autor autorActualizado) {
        if (!autores.containsKey(id)) {
            return Optional.empty();
        }
        autorActualizado.setId(id);
        autores.put(id, autorActualizado);
        return Optional.of(autorActualizado);
    }
}
