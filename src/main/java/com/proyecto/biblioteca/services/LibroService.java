package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Libro;
import com.proyecto.biblioteca.repositories.LibroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LibroService {

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Transactional(readOnly = true)
    public List<Libro> listarTodos() {
        return libroRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Libro> buscarPorIsbn(String isbn) {
        if (isbn == null) return Optional.empty();
        return libroRepository.findByIsbnAndActivoTrue(isbn.trim());
    }

    public Libro guardar(Libro libro) {
        if (libro.getIsbn() != null) {
            libro.setIsbn(libro.getIsbn().trim());
        }
        if (libro.getActivo() == null) {
            libro.setActivo(true);
        }
        return libroRepository.save(libro);
    }

    public Optional<Libro> actualizar(String isbn, Libro libroActualizado) {
        if (isbn == null) return Optional.empty();
        return libroRepository.findByIsbnAndActivoTrue(isbn.trim()).map(existente -> {
            existente.setTitulo(libroActualizado.getTitulo());
            existente.setSinopsis(libroActualizado.getSinopsis());
            existente.setIdAutor(libroActualizado.getIdAutor());
            existente.setIdCategoria(libroActualizado.getIdCategoria());
            existente.setIdEditorial(libroActualizado.getIdEditorial());
            existente.setAnioPublicacion(libroActualizado.getAnioPublicacion());
            return libroRepository.save(existente);
        });
    }

    public boolean eliminar(String isbn) {
        if (isbn == null) return false;
        return libroRepository.findByIsbnAndActivoTrue(isbn.trim()).map(existente -> {
            existente.setActivo(false);
            libroRepository.save(existente);
            return true;
        }).orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean existe(String isbn) {
        return isbn != null && libroRepository.findByIsbnAndActivoTrue(isbn.trim()).isPresent();
    }
}
