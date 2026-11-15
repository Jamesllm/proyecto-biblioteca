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
        return autorRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Autor> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return autorRepository.findByIdAndActivoTrue(id);
    }

    public Autor guardar(Autor autor) {
        if (autor.getActivo() == null) {
            autor.setActivo(true);
        }
        return autorRepository.save(autor);
    }

    public Optional<Autor> actualizar(Long id, Autor autorActualizado) {
        return autorRepository.findByIdAndActivoTrue(id).map(existente -> {
            existente.setNombre(autorActualizado.getNombre());
            existente.setNacionalidad(autorActualizado.getNacionalidad());
            existente.setBiografia(autorActualizado.getBiografia());
            return autorRepository.save(existente);
        });
    }

    public boolean eliminar(Long id) {
        if (id == null) return false;
        return autorRepository.findByIdAndActivoTrue(id).map(autor -> {
            autor.setActivo(false);
            autorRepository.save(autor);
            return true;
        }).orElse(false);
    }
}
