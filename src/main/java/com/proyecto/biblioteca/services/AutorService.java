package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Autor;
import com.proyecto.biblioteca.repositories.AutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AutorService {

    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    @Transactional(readOnly = true)
    public List<Autor> listarTodos() {
        return autorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Autor> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return autorRepository.findById(id);
    }

    public Autor guardar(Autor autor) {
        return autorRepository.save(autor);
    }

    public Optional<Autor> actualizar(Long id, Autor autorActualizado) {
        return autorRepository.findById(id).map(existente -> {
            existente.setNombre(autorActualizado.getNombre());
            existente.setNacionalidad(autorActualizado.getNacionalidad());
            existente.setBiografia(autorActualizado.getBiografia());
            return autorRepository.save(existente);
        });
    }

    public boolean eliminar(Long id) {
        if (id != null && autorRepository.existsById(id)) {
            autorRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
