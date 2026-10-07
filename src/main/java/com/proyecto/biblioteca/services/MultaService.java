package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Multa;
import com.proyecto.biblioteca.repositories.MultaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MultaService {

    private final MultaRepository multaRepository;

    public MultaService(MultaRepository multaRepository) {
        this.multaRepository = multaRepository;
    }

    @Transactional(readOnly = true)
    public List<Multa> listarTodas() {
        return multaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Multa> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return multaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Multa> listarPorUsuario(Long idUsuario) {
        if (idUsuario == null) return List.of();
        return multaRepository.findByIdUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Multa> listarPendientesPorUsuario(Long idUsuario) {
        if (idUsuario == null) return List.of();
        return multaRepository.findByIdUsuarioAndEstadoIgnoreCase(idUsuario, "PENDIENTE");
    }

    @Transactional(readOnly = true)
    public Optional<Multa> buscarPorPrestamo(Long idPrestamo) {
        if (idPrestamo == null) return Optional.empty();
        return multaRepository.findByIdPrestamo(idPrestamo);
    }

    public Multa generarMultaPorRetraso(Long idPrestamo, Long idUsuario, long horasRetraso, int diasRetraso, double monto) {
        Multa multa = new Multa();
        multa.setIdPrestamo(idPrestamo);
        multa.setIdUsuario(idUsuario);
        multa.setTipoMulta("RETRASO");
        multa.setHorasRetraso(horasRetraso);
        multa.setDiasRetraso(diasRetraso);
        multa.setMonto(monto);
        multa.setMotivo("Retraso en devolución de " + diasRetraso + " día(s) (" + horasRetraso + " hrs)");
        multa.setFechaHoraEmision(LocalDateTime.now());
        multa.setEstado("PENDIENTE");
        return multaRepository.save(multa);
    }

    public Multa generarMultaPorDano(Long idPrestamo, Long idUsuario, double monto, String motivo) {
        Multa multa = new Multa();
        multa.setIdPrestamo(idPrestamo);
        multa.setIdUsuario(idUsuario);
        multa.setTipoMulta("DAÑO");
        multa.setMonto(monto);
        multa.setMotivo(motivo != null && !motivo.isBlank() ? motivo : "Deterioro/Daño en ejemplar retornado");
        multa.setFechaHoraEmision(LocalDateTime.now());
        multa.setEstado("PENDIENTE");
        return multaRepository.save(multa);
    }

    public Multa registrarMulta(Multa multa) {
        if (multa.getIdUsuario() == null) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio para registrar una multa");
        }
        if (multa.getMonto() == null || multa.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto de la multa debe ser mayor a 0");
        }
        if (multa.getFechaHoraEmision() == null) {
            multa.setFechaHoraEmision(LocalDateTime.now());
        }
        if (multa.getEstado() == null || multa.getEstado().isBlank()) {
            multa.setEstado("PENDIENTE");
        }
        return multaRepository.save(multa);
    }

    public Optional<Multa> actualizar(Long id, Multa multaActualizada) {
        if (id == null) return Optional.empty();
        return multaRepository.findById(id).map(existente -> {
            existente.setMonto(multaActualizada.getMonto());
            existente.setMotivo(multaActualizada.getMotivo());
            existente.setTipoMulta(multaActualizada.getTipoMulta());
            existente.setEstado(multaActualizada.getEstado());
            return multaRepository.save(existente);
        });
    }

    public boolean marcarComoPagada(Long id) {
        if (id == null) return false;
        return multaRepository.findById(id).map(m -> {
            m.setEstado("PAGADA");
            multaRepository.save(m);
            return true;
        }).orElse(false);
    }

    public boolean anularMulta(Long id) {
        if (id == null) return false;
        return multaRepository.findById(id).map(m -> {
            m.setEstado("ANULADA");
            multaRepository.save(m);
            return true;
        }).orElse(false);
    }

    public boolean eliminar(Long id) {
        if (id != null && multaRepository.existsById(id)) {
            multaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Multa guardar(Multa multa) {
        return multaRepository.save(multa);
    }
}
