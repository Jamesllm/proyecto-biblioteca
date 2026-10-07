package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Editorial;
import com.proyecto.biblioteca.repositories.EditorialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EditorialService {

    private final EditorialRepository editorialRepository;

    public EditorialService(EditorialRepository editorialRepository) {
        this.editorialRepository = editorialRepository;
    }

    @Transactional(readOnly = true)
    public List<Editorial> listarTodas() {
        return editorialRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Editorial> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return editorialRepository.findByIdAndActivoTrue(id);
    }

    public Editorial guardar(Editorial editorial) {
        if (editorial.getActivo() == null) {
            editorial.setActivo(true);
        }
        return editorialRepository.save(editorial);
    }

    public Optional<Editorial> actualizar(Long id, Editorial editorialActualizada) {
        return editorialRepository.findByIdAndActivoTrue(id).map(existente -> {
            existente.setNombre(editorialActualizada.getNombre());
            existente.setPais(editorialActualizada.getPais());
            existente.setContacto(editorialActualizada.getContacto());
            return editorialRepository.save(existente);
        });
    }

    public boolean eliminar(Long id) {
        if (id == null) return false;
        return editorialRepository.findByIdAndActivoTrue(id).map(editorial -> {
            editorial.setActivo(false);
            editorialRepository.save(editorial);
            return true;
        }).orElse(false);
    }
}
