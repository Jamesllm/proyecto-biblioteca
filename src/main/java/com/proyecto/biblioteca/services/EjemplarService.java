package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Ejemplar;
import com.proyecto.biblioteca.models.Libro;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class EjemplarService {
    private final ConcurrentHashMap<Long, Ejemplar> ejemplares = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final LibroService libroService;

    public EjemplarService(LibroService libroService) {
        this.libroService = libroService;

        // Datos iniciales de ejemplares físicos vinculados a los ISBNs
        guardar(new Ejemplar(null, "978-0307474728", 1, "Estantería A-1, Nivel 1", "EXCELENTE", "DISPONIBLE", null));
        guardar(new Ejemplar(null, "978-0307474728", 2, "Estantería A-1, Nivel 1", "BUENO", "DISPONIBLE", null));
        guardar(new Ejemplar(null, "978-8420471839", 1, "Estantería A-2, Nivel 2", "BUENO", "DISPONIBLE", null));
        guardar(new Ejemplar(null, "978-8420471839", 2, "Estantería A-2, Nivel 2", "REGULAR", "DISPONIBLE", null));
        guardar(new Ejemplar(null, "978-0307950925", 1, "Estantería B-1, Nivel 3", "EXCELENTE", "DISPONIBLE", null));
        guardar(new Ejemplar(null, "978-0307389732", 1, "Estantería B-2, Nivel 1", "BUENO", "DISPONIBLE", null));
        guardar(new Ejemplar(null, "978-8401342653", 1, "Estantería C-1, Nivel 2", "EXCELENTE", "DISPONIBLE", null));
        guardar(new Ejemplar(null, "978-8420471891", 1, "Estantería C-2, Nivel 4", "BUENO", "DISPONIBLE", null));
    }

    public List<Ejemplar> listarTodos() {
        return ejemplares.values().stream()
                .map(this::enriquecerConLibro)
                .collect(Collectors.toList());
    }

    public Optional<Ejemplar> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(ejemplares.get(id)).map(this::enriquecerConLibro);
    }

    public List<Ejemplar> listarPorIsbn(String isbn) {
        if (isbn == null) return new ArrayList<>();
        return ejemplares.values().stream()
                .filter(e -> isbn.trim().equalsIgnoreCase(e.getIsbn()))
                .map(this::enriquecerConLibro)
                .collect(Collectors.toList());
    }

    public List<Ejemplar> listarDisponiblesPorIsbn(String isbn) {
        if (isbn == null) return new ArrayList<>();
        return ejemplares.values().stream()
                .filter(e -> isbn.trim().equalsIgnoreCase(e.getIsbn()) && e.isDisponible())
                .map(this::enriquecerConLibro)
                .collect(Collectors.toList());
    }

    public Ejemplar guardar(Ejemplar ejemplar) {
        if (ejemplar.getId() == null || ejemplar.getId() <= 0) {
            ejemplar.setId(idGenerator.incrementAndGet());
        } else {
            idGenerator.updateAndGet(current -> Math.max(current, ejemplar.getId()));
        }

        if (ejemplar.getEstado() == null || ejemplar.getEstado().isBlank()) {
            ejemplar.setEstado("DISPONIBLE");
        }
        if (ejemplar.getEstadoConservacion() == null || ejemplar.getEstadoConservacion().isBlank()) {
            ejemplar.setEstadoConservacion("BUENO");
        }

        // Vincular el objeto Libro
        enriquecerConLibro(ejemplar);
        ejemplares.put(ejemplar.getId(), ejemplar);
        return ejemplar;
    }

    public Optional<Ejemplar> actualizar(Long id, Ejemplar ejemplarActualizado) {
        if (id == null || !ejemplares.containsKey(id)) {
            return Optional.empty();
        }
        ejemplarActualizado.setId(id);
        enriquecerConLibro(ejemplarActualizado);
        ejemplares.put(id, ejemplarActualizado);
        return Optional.of(ejemplarActualizado);
    }

    public boolean cambiarEstado(Long id, String nuevoEstado) {
        Ejemplar ejemplar = ejemplares.get(id);
        if (ejemplar != null) {
            ejemplar.setEstado(nuevoEstado);
            return true;
        }
        return false;
    }

    public boolean cambiarEstadoConservacion(Long id, String nuevoEstadoConservacion) {
        Ejemplar ejemplar = ejemplares.get(id);
        if (ejemplar != null) {
            ejemplar.setEstadoConservacion(nuevoEstadoConservacion);
            return true;
        }
        return false;
    }

    public boolean eliminar(Long id) {
        if (id == null) return false;
        return ejemplares.remove(id) != null;
    }

    private Ejemplar enriquecerConLibro(Ejemplar ejemplar) {
        if (ejemplar != null && ejemplar.getIsbn() != null) {
            libroService.buscarPorIsbn(ejemplar.getIsbn()).ifPresent(ejemplar::setLibro);
        }
        return ejemplar;
    }
}
