package com.proyecto.biblioteca.services;

import com.proyecto.biblioteca.models.Reserva;
import com.proyecto.biblioteca.repositories.LibroRepository;
import com.proyecto.biblioteca.repositories.ReservaRepository;
import com.proyecto.biblioteca.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaService(ReservaRepository reservaRepository,
                          LibroRepository libroRepository,
                          UsuarioRepository usuarioRepository) {
        this.reservaRepository = reservaRepository;
        this.libroRepository = libroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarTodas() {
        return reservaRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Reserva> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return reservaRepository.findByIdAndActivoTrue(id);
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarPorUsuario(Long idUsuario) {
        if (idUsuario == null) return List.of();
        return reservaRepository.findByIdUsuarioAndActivoTrue(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarPorIsbn(String isbn) {
        if (isbn == null) return List.of();
        return reservaRepository.findByIsbnAndActivoTrue(isbn.trim());
    }

    public Reserva crearReserva(Reserva reserva) {
        if (reserva.getIsbn() == null || reserva.getIdUsuario() == null) {
            throw new IllegalArgumentException("El ISBN del libro y el ID del usuario son obligatorios.");
        }

        if (usuarioRepository.findByIdAndActivoTrue(reserva.getIdUsuario()).isEmpty()) {
            throw new IllegalArgumentException("El usuario con ID " + reserva.getIdUsuario() + " no existe o está inactivo.");
        }

        if (libroRepository.findByIsbnAndActivoTrue(reserva.getIsbn().trim()).isEmpty()) {
            throw new IllegalArgumentException("El libro con ISBN " + reserva.getIsbn() + " no existe o está inactivo.");
        }

        if (reserva.getFechaHoraReserva() == null) {
            reserva.setFechaHoraReserva(LocalDateTime.now());
        }
        if (reserva.getFechaHoraExpiracion() == null) {
            reserva.setFechaHoraExpiracion(reserva.getFechaHoraReserva().plusDays(3));
        }
        reserva.setEstado("PENDIENTE");
        reserva.setActivo(true);

        return reservaRepository.save(reserva);
    }

    public boolean cancelarReserva(Long id) {
        if (id == null) return false;
        return reservaRepository.findByIdAndActivoTrue(id).map(r -> {
            r.setEstado("CANCELADA");
            reservaRepository.save(r);
            return true;
        }).orElse(false);
    }

    public boolean atenderReserva(Long id) {
        if (id == null) return false;
        return reservaRepository.findByIdAndActivoTrue(id).map(r -> {
            r.setEstado("ATENDIDA");
            reservaRepository.save(r);
            return true;
        }).orElse(false);
    }

    public boolean eliminar(Long id) {
        if (id == null) return false;
        return reservaRepository.findByIdAndActivoTrue(id).map(r -> {
            r.setActivo(false);
            reservaRepository.save(r);
            return true;
        }).orElse(false);
    }
}
