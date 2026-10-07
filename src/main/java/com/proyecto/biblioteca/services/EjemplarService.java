package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Ejemplar;
import com.proyecto.biblioteca.repositories.EjemplarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EjemplarService {

    private final EjemplarRepository ejemplarRepository;

    public EjemplarService(EjemplarRepository ejemplarRepository) {
        this.ejemplarRepository = ejemplarRepository;
    }

    @Transactional(readOnly = true)
    public List<Ejemplar> listarTodos() {
        return ejemplarRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Ejemplar> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return ejemplarRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Ejemplar> listarPorIsbn(String isbn) {
        if (isbn == null) return List.of();
        return ejemplarRepository.findByIsbn(isbn.trim());
    }

    @Transactional(readOnly = true)
    public List<Ejemplar> listarDisponiblesPorIsbn(String isbn) {
        if (isbn == null) return List.of();
        return ejemplarRepository.findByIsbnAndEstadoIgnoreCase(isbn.trim(), "DISPONIBLE");
    }

    public Ejemplar guardar(Ejemplar ejemplar) {
        if (ejemplar.getEstado() == null || ejemplar.getEstado().isBlank()) {
            ejemplar.setEstado("DISPONIBLE");
        }
        if (ejemplar.getEstadoConservacion() == null || ejemplar.getEstadoConservacion().isBlank()) {
            ejemplar.setEstadoConservacion("BUENO");
        }
        return ejemplarRepository.save(ejemplar);
    }

    public Optional<Ejemplar> actualizar(Long id, Ejemplar ejemplarActualizado) {
        if (id == null) return Optional.empty();
        return ejemplarRepository.findById(id).map(existente -> {
            existente.setIsbn(ejemplarActualizado.getIsbn());
            existente.setNumeroCopia(ejemplarActualizado.getNumeroCopia());
            existente.setUbicacion(ejemplarActualizado.getUbicacion());
            existente.setEstadoConservacion(ejemplarActualizado.getEstadoConservacion());
            existente.setEstado(ejemplarActualizado.getEstado());
            return ejemplarRepository.save(existente);
        });
    }

    public boolean cambiarEstado(Long id, String nuevoEstado) {
        if (id == null) return false;
        return ejemplarRepository.findById(id).map(e -> {
            e.setEstado(nuevoEstado);
            ejemplarRepository.save(e);
            return true;
        }).orElse(false);
    }

    public boolean cambiarEstadoConservacion(Long id, String nuevoEstadoConservacion) {
        if (id == null) return false;
        return ejemplarRepository.findById(id).map(e -> {
            e.setEstadoConservacion(nuevoEstadoConservacion);
            ejemplarRepository.save(e);
            return true;
        }).orElse(false);
    }

    public boolean eliminar(Long id) {
        if (id != null && ejemplarRepository.existsById(id)) {
            ejemplarRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
