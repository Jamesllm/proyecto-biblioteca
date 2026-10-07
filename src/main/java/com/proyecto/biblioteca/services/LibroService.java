package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Libro;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class LibroService {
    private final ConcurrentHashMap<Long, Libro> libros = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public LibroService() {
        // Datos iniciales de prueba (mínimo 5 registros)
        guardar(new Libro(null, "Cien años de soledad", "978-0307474728", 1L, 1L, 1L, 1967, true));
        guardar(new Libro(null, "La ciudad y los perros", "978-8420471839", 2L, 1L, 2L, 1963, true));
        guardar(new Libro(null, "Ficciones", "978-0307950925", 3L, 1L, 3L, 1944, true));
        guardar(new Libro(null, "El amor en los tiempos del cólera", "978-0307389732", 1L, 1L, 1L, 1985, true));
        guardar(new Libro(null, "La casa de los espíritus", "978-8401342653", 4L, 1L, 3L, 1982, true));
        guardar(new Libro(null, "Rayuela", "978-8420471891", 5L, 1L, 2L, 1963, true));
    }

    public List<Libro> listarTodos() {
        return new ArrayList<>(libros.values());
    }

    public Optional<Libro> buscarPorId(Long id) {
        return Optional.ofNullable(libros.get(id));
    }

    public Libro guardar(Libro libro) {
        if (libro.getId() == null || libro.getId() <= 0) {
            libro.setId(idGenerator.incrementAndGet());
        } else {
            idGenerator.updateAndGet(current -> Math.max(current, libro.getId()));
        }
        libros.put(libro.getId(), libro);
        return libro;
    }

    public Optional<Libro> actualizar(Long id, Libro libroActualizado) {
        Libro libroExistente = libros.get(id);
        if (libroExistente == null) {
            return Optional.empty();
        }
        libroActualizado.setId(id);
        if (libroActualizado.getDisponible() == null) {
            libroActualizado.setDisponible(libroExistente.isDisponible());
        }
        libros.put(id, libroActualizado);
        return Optional.of(libroActualizado);
    }

    public boolean actualizarDisponibilidad(Long id, boolean disponible) {
        Libro libro = libros.get(id);
        if (libro != null) {
            libro.setDisponible(disponible);
            return true;
        }
        return false;
    }
}
