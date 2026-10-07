package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Categoria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CategoriaService {
    private final ConcurrentHashMap<Long, Categoria> categorias = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public CategoriaService() {
        // Datos iniciales de prueba (mínimo 5 registros)
        guardar(new Categoria(null, "Novela y Ficción", "Obras literarias de narrativa y ficción general."));
        guardar(new Categoria(null, "Ciencia y Tecnología", "Libros dedicados a informática, ciencias exactas e ingeniería."));
        guardar(new Categoria(null, "Historia y Filosofía", "Estudios históricos, ensayos y obras de pensamiento crítico."));
        guardar(new Categoria(null, "Poesía y Drama", "Colecciones poéticas, dramaturgia y obras teatrales."));
        guardar(new Categoria(null, "Ciencias Sociales y Educación", "Textos de sociología, pedagogía, psicología y educación."));
    }

    public List<Categoria> listarTodas() {
        return new ArrayList<>(categorias.values());
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return Optional.ofNullable(categorias.get(id));
    }

    public Categoria guardar(Categoria categoria) {
        if (categoria.getId() == null || categoria.getId() <= 0) {
            categoria.setId(idGenerator.incrementAndGet());
        } else {
            idGenerator.updateAndGet(current -> Math.max(current, categoria.getId()));
        }
        categorias.put(categoria.getId(), categoria);
        return categoria;
    }

    public Optional<Categoria> actualizar(Long id, Categoria categoriaActualizada) {
        if (!categorias.containsKey(id)) {
            return Optional.empty();
        }
        categoriaActualizada.setId(id);
        categorias.put(id, categoriaActualizada);
        return Optional.of(categoriaActualizada);
    }
}
