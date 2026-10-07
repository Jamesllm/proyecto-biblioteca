package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Categoria;
import com.proyecto.biblioteca.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return categoriaRepository.findById(id);
    }

    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public Optional<Categoria> actualizar(Long id, Categoria categoriaActualizada) {
        return categoriaRepository.findById(id).map(existente -> {
            existente.setNombre(categoriaActualizada.getNombre());
            existente.setDescripcion(categoriaActualizada.getDescripcion());
            return categoriaRepository.save(existente);
        });
    }

    public boolean eliminar(Long id) {
        if (id != null && categoriaRepository.existsById(id)) {
            categoriaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
